package com.library.backend.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.backend.dto.BubbleSortRequest;
import com.library.backend.dto.HashRequest;
import com.library.backend.model.CallLog;
import com.library.backend.model.User;
import com.library.backend.repository.CallLogRepository;
import com.library.backend.repository.UserRepository;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用埋点切面。
 *
 * <p>拦截 {@code AlgorithmController} 全部方法（算法接口，不含 export），自动写入 {@code CallLog}：
 * <ul>
 *   <li>{@code @AfterReturning}：成功返回 → responseStatus=SUCCESS，记录 responseData</li>
 *   <li>{@code @AfterThrowing}：方法抛异常 → responseStatus=ERROR，记录 requestSummary
 *       （spec tracking-analytics.md 要求异常时同样埋点）</li>
 * </ul>
 * 切面自身异常（User 查询失败 / CallLog 写入失败）try-catch 兜底，仅记 WARN 日志，
 * <b>绝不阻断主流程</b>——埋点是旁路逻辑。</p>
 *
 * <p><b>设计-规格对齐</b>：design.md 原仅写 @AfterReturning，但 spec 要求异常也埋点，
 * 故补充 @AfterThrowing。属意图不变的一致性更新，已同步 design.md。</p>
 */
@Aspect
@Component
public class CallLogAspect {

    private static final Logger log = LoggerFactory.getLogger(CallLogAspect.class);

    /** responseData 最大长度（CallLog 字段 length=2000） */
    private static final int MAX_DATA_LEN = 1900;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final CallLogRepository callLogRepository;
    private final UserRepository userRepository;

    public CallLogAspect(CallLogRepository callLogRepository, UserRepository userRepository) {
        this.callLogRepository = callLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * 成功返回后埋点。
     */
    @AfterReturning(pointcut = "execution(* com.library.backend.controller.AlgorithmController.*(..)) && " +
                              "!execution(* com.library.backend.controller.AlgorithmController.export(..))",
                    returning = "result")
    public void logSuccess(JoinPoint jp, Object result) {
        writeCallLog(jp, result, null);
    }

    /**
     * 异常抛出后埋点（spec 要求异常时记录 ERROR 状态）。
     *
     * <p>异常会继续向外抛（由 GlobalExceptionHandler 转 400/500），此切面仅旁路记录。</p>
     */
    @AfterThrowing(pointcut = "execution(* com.library.backend.controller.AlgorithmController.*(..)) && " +
                              "!execution(* com.library.backend.controller.AlgorithmController.export(..))",
                    throwing = "ex")
    public void logError(JoinPoint jp, Throwable ex) {
        writeCallLog(jp, null, ex);
    }

    private void writeCallLog(JoinPoint jp, Object result, Throwable ex) {
        try {
            String methodName = jp.getSignature().getName();
            String apiType = resolveApiType(methodName);

            HttpServletRequest request = currentRequest();
            Long userId = readUserId(request);
            String requestSummary = buildRequestSummary(jp, request);

            CallLog callLog = new CallLog();
            callLog.setApiType(apiType);
            callLog.setCallerUserId(userId);

            if (userId != null) {
                userRepository.findById(userId).ifPresent(user -> {
                    callLog.setCallerUsername(user.getUsername());
                    callLog.setPersonnelType(user.getPersonnelType());
                    callLog.setPersonnelLevel(user.getPersonnelLevel());
                    callLog.setDepartment(user.getDepartment());
                });
            }

            callLog.setRequestSummary(truncate(requestSummary));
            if (ex == null) {
                callLog.setResponseStatus("SUCCESS");
                callLog.setResponseData(truncate(safeToJson(result)));
            } else {
                callLog.setResponseStatus("ERROR");
                callLog.setResponseData(truncate(ex.getClass().getSimpleName() + ": " + ex.getMessage()));
            }

            callLogRepository.save(callLog);
        } catch (Exception e) {
            // 兜底：埋点失败仅记日志，绝不阻断主流程
            log.warn("埋点写入失败，不阻断主流程: {}", e.getMessage());
        }
    }

    private String resolveApiType(String methodName) {
        return switch (methodName) {
            case "helloworld" -> "helloworld";
            case "hash" -> "hash";
            case "bubbleSort" -> "bubble-sort";
            default -> methodName;
        };
    }

    private Long readUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String header = request.getHeader("X-User-Id");
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String buildRequestSummary(JoinPoint jp, HttpServletRequest request) {
        Object[] args = jp.getArgs();
        for (Object arg : args) {
            // 第一个非 Long 类型参数通常是请求体
            if (arg instanceof HashRequest hr) {
                return "hash: text=" + hr.getText() + ", algorithm=" + hr.getAlgorithm();
            }
            if (arg instanceof BubbleSortRequest br) {
                return "bubble-sort: numbers=" + br.getNumbers();
            }
        }
        if (request != null) {
            String query = request.getQueryString();
            return query == null ? request.getMethod() + " " + request.getRequestURI()
                                 : request.getMethod() + " " + request.getRequestURI() + "?" + query;
        }
        return jp.getSignature().toShortString();
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs.getRequest();
    }

    private String safeToJson(Object result) {
        try {
            String json = objectMapper.writeValueAsString(result);
            return json;
        } catch (Exception e) {
            return String.valueOf(result);
        }
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= MAX_DATA_LEN ? value : value.substring(0, MAX_DATA_LEN);
    }
}

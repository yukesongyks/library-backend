package com.example.library.aspect;

import com.example.library.model.ApiCallLog;
import com.example.library.model.User;
import com.example.library.repository.ApiCallLogRepository;
import com.example.library.repository.UserRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 调用埋点切面：拦截 @TrackCall 标注的三接口方法，记录调用日志。
 * 对应 design.md 3.2 / tasks D1-D3。
 * 切面异常不影响业务调用。
 */
@Aspect
@Component
public class CallLogAspect {

    private static final Logger log = LoggerFactory.getLogger(CallLogAspect.class);
    private static final String UNKNOWN = "unknown";

    private final ApiCallLogRepository apiCallLogRepository;
    private final UserRepository userRepository;

    public CallLogAspect(ApiCallLogRepository apiCallLogRepository, UserRepository userRepository) {
        this.apiCallLogRepository = apiCallLogRepository;
        this.userRepository = userRepository;
    }

    @Around("@annotation(trackCall)")
    public Object track(ProceedingJoinPoint pjp, TrackCall trackCall) throws Throwable {
        String apiName = trackCall.value();
        long start = System.currentTimeMillis();
        String status = "SUCCESS";
        Object result;
        try {
            result = pjp.proceed();
        } catch (Throwable ex) {
            status = "FAILED";
            throw ex;
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            try {
                saveLog(apiName, status, durationMs);
            } catch (Exception logEx) {
                log.error("埋点写入失败，不影响业务: apiName={}, err={}", apiName, logEx.getMessage());
            }
        }
        return result;
    }

    private void saveLog(String apiName, String status, long durationMs) {
        Long userId = null;
        String username = null;
        String userType = UNKNOWN;
        String userLevel = UNKNOWN;
        String department = UNKNOWN;

        HttpServletRequest request = currentRequest();
        if (request != null) {
            String header = request.getHeader("X-User-Id");
            if (header != null && !header.isEmpty()) {
                try {
                    Long parsedId = Long.parseLong(header);
                    Optional<User> userOpt = userRepository.findById(parsedId);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        userId = user.getId();
                        username = user.getUsername();
                        userType = user.getUserType() != null ? user.getUserType() : UNKNOWN;
                        userLevel = user.getUserLevel() != null ? user.getUserLevel() : UNKNOWN;
                        department = user.getDepartment() != null ? user.getDepartment() : UNKNOWN;
                    }
                } catch (NumberFormatException ignored) {
                    // header 非数字，维度保持 unknown
                }
            }
        }

        ApiCallLog logEntry = new ApiCallLog(apiName, userId, username, userType,
                userLevel, department, status, durationMs, LocalDateTime.now());
        apiCallLogRepository.save(logEntry);
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}

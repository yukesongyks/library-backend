package com.library.aspect;

import com.library.entity.AppUser;
import com.library.entity.CallLog;
import com.library.repository.AppUserRepository;
import com.library.repository.CallLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class CallLogAspect {
    private static final Logger log = LoggerFactory.getLogger(CallLogAspect.class);

    private final CallLogRepository callLogRepository;
    private final AppUserRepository appUserRepository;

    public CallLogAspect(CallLogRepository callLogRepository,
                         AppUserRepository appUserRepository) {
        this.callLogRepository = callLogRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * 仅切入 AlgoController 的算法接口，避免统计/导出接口自污染（B4）。
     */
    @Around("execution(* com.library.controller.AlgoController.*(..))")
    public Object logCall(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        boolean success = true;
        Throwable error = null;
        try {
            result = pjp.proceed();
        } catch (Throwable t) {
            success = false;
            error = t;
            throw t;
        } finally {
            // B1: 业务异常后仍执行埋点，区分成功/失败状态
            try {
                String apiName = pjp.getSignature().toShortString();
                String callerId = resolveCallerId();
                AppUser user = appUserRepository.findByUserId(callerId).orElse(null);

                CallLog logEntry = new CallLog();
                logEntry.setApiName(apiName);
                logEntry.setCallerId(callerId);
                logEntry.setCallerName(user != null ? user.getUserName() : "匿名");
                logEntry.setUserType(user != null ? user.getUserType() : "UNKNOWN");
                logEntry.setUserLevel(user != null ? user.getUserLevel() : "UNKNOWN");
                logEntry.setDepartment(user != null ? user.getDepartment() : "UNKNOWN");
                logEntry.setCalledAt(LocalDateTime.now());
                logEntry.setSuccess(success);
                logEntry.setErrorMsg(success ? null : (error != null ? error.getClass().getSimpleName() + ": " + error.getMessage() : "unknown"));
                callLogRepository.save(logEntry);
            } catch (Exception e) {
                // 埋点自身异常至少记录日志，不再静默吞没
                log.warn("埋点记录失败: {}", e.getMessage(), e);
            }
        }
        return result;
    }

    private String resolveCallerId() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            HttpServletRequest req = sra.getRequest();
            String uid = req.getHeader("X-User-Id");
            // B1: 无用户标识时返回 ANONYMOUS 而非伪造 U001
            return uid != null ? uid : "ANONYMOUS";
        }
        return "ANONYMOUS";
    }
}

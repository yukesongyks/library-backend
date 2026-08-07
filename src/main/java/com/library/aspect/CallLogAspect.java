package com.library.aspect;

import com.library.entity.AppUser;
import com.library.entity.CallLog;
import com.library.repository.AppUserRepository;
import com.library.repository.CallLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class CallLogAspect {
    private final CallLogRepository callLogRepository;
    private final AppUserRepository appUserRepository;

    public CallLogAspect(CallLogRepository callLogRepository,
                         AppUserRepository appUserRepository) {
        this.callLogRepository = callLogRepository;
        this.appUserRepository = appUserRepository;
    }

    @Around("execution(* com.library.controller..*.*(..))")
    public Object logCall(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        try {
            String apiName = pjp.getSignature().toShortString();
            String callerId = resolveCallerId();
            AppUser user = appUserRepository.findByUserId(callerId).orElse(null);

            CallLog log = new CallLog();
            log.setApiName(apiName);
            log.setCallerId(callerId);
            log.setCallerName(user != null ? user.getUserName() : "匿名");
            log.setUserType(user != null ? user.getUserType() : "UNKNOWN");
            log.setUserLevel(user != null ? user.getUserLevel() : "UNKNOWN");
            log.setDepartment(user != null ? user.getDepartment() : "UNKNOWN");
            log.setCalledAt(LocalDateTime.now());
            callLogRepository.save(log);
        } catch (Exception ignored) {
        }
        return result;
    }

    private String resolveCallerId() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            HttpServletRequest req = sra.getRequest();
            String uid = req.getHeader("X-User-Id");
            return uid != null ? uid : "U001";
        }
        return "U001";
    }
}

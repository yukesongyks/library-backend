package com.library.demo.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo.model.entity.ApiCallLog;
import com.library.demo.repository.ApiCallLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class ApiCallLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ApiCallLogAspect.class);
    private final ApiCallLogRepository repository;
    private final ObjectMapper objectMapper;

    public ApiCallLogAspect(ApiCallLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Around("execution(* com.library.demo.controller.DemoController.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            try {
                String methodName = joinPoint.getSignature().getName();
                String apiType = mapMethodToApiType(methodName);
                Object[] args = joinPoint.getArgs();
                String requestPayload = args.length > 0 ? objectMapper.writeValueAsString(args[0]) : "{}";
                String responsePayload = (result != null && error == null)
                        ? objectMapper.writeValueAsString(result) : "{}";

                saveLogAsync(apiType, requestPayload, responsePayload, duration);
            } catch (Exception e) {
                log.warn("Failed to log API call: {}", e.getMessage());
            }
        }
    }

    @Async
    public void saveLogAsync(String apiType, String requestPayload, String responsePayload, long duration) {
        try {
            ApiCallLog logEntry = new ApiCallLog();
            logEntry.setApiType(apiType);
            logEntry.setRequestPayload(truncate(requestPayload, 4000));
            logEntry.setResponsePayload(truncate(responsePayload, 4000));
            logEntry.setDurationMs(duration);
            logEntry.setCreatedAt(LocalDateTime.now());

            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                logEntry.setUserId(request.getHeader("X-User-Id"));
                logEntry.setUserName(request.getHeader("X-User-Name"));
                logEntry.setPersonnelType(request.getHeader("X-User-Type"));
                logEntry.setPersonnelLevel(request.getHeader("X-User-Level"));
                logEntry.setDepartment(request.getHeader("X-User-Dept"));
            }

            repository.save(logEntry);
        } catch (Exception e) {
            log.error("Async save failed: {}", e.getMessage());
        }
    }

    private String mapMethodToApiType(String methodName) {
        return switch (methodName) {
            case "helloWorld" -> "helloworld";
            case "hash" -> "hash";
            case "bubbleSort" -> "bubble-sort";
            default -> "unknown";
        };
    }

    private String truncate(String value, int maxLen) {
        if (value == null) return null;
        return value.length() > maxLen ? value.substring(0, maxLen) : value;
    }
}

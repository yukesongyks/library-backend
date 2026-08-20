package com.library.demo.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo.service.ApiCallLogAsyncWriter;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class ApiCallLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ApiCallLogAspect.class);
    private final ApiCallLogAsyncWriter asyncWriter;
    private final ObjectMapper objectMapper;

    public ApiCallLogAspect(ApiCallLogAsyncWriter asyncWriter, ObjectMapper objectMapper) {
        this.asyncWriter = asyncWriter;
        this.objectMapper = objectMapper;
    }

    @Around("execution(* com.library.demo.controller.DemoController.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;

        // 在主线程中提取请求头用户信息，避免异步线程中 RequestContextHolder 为 null
        String userId = null;
        String userName = null;
        String personnelType = null;
        String personnelLevel = null;
        String department = null;
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                userId = request.getHeader("X-User-Id");
                userName = request.getHeader("X-User-Name");
                personnelType = request.getHeader("X-User-Type");
                personnelLevel = request.getHeader("X-User-Level");
                department = request.getHeader("X-User-Dept");
            }
        } catch (Exception e) {
            log.warn("Failed to extract request headers for logging: {}", e.getMessage());
        }

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

                asyncWriter.saveLogAsync(apiType, requestPayload, responsePayload, duration,
                        userId, userName, personnelType, personnelLevel, department);
            } catch (Exception e) {
                log.warn("Failed to log API call: {}", e.getMessage());
            }
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
}

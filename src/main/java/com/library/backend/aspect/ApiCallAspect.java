package com.library.backend.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.backend.dto.ApiRequest;
import com.library.backend.entity.ApiCallLog;
import com.library.backend.repository.ApiCallLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiCallAspect {
    
    private final ApiCallLogRepository apiCallLogRepository;
    private final ObjectMapper objectMapper;
    
    @Around("execution(* com.library.backend.controller.*Controller.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String method = request.getMethod();
                String uri = request.getRequestURI();
                
                // Extract API name from URI
                String apiName = extractApiName(uri);
                
                // Try to extract user info from request body
                Object[] args = joinPoint.getArgs();
                ApiRequest apiRequest = null;
                for (Object arg : args) {
                    if (arg instanceof ApiRequest) {
                        apiRequest = (ApiRequest) arg;
                        break;
                    }
                }
                
                if (apiRequest != null && apiName != null) {
                    saveLogAsync(apiRequest, apiName);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to log API call", e);
        }
        
        return result;
    }
    
    private String extractApiName(String uri) {
        if (uri.contains("/hello")) return "hello";
        if (uri.contains("/hash")) return "hash";
        if (uri.contains("/bubble-sort")) return "bubble-sort";
        if (uri.contains("/export")) return "export";
        if (uri.contains("/stats")) return "stats";
        return null;
    }
    
    @Async
    public void saveLogAsync(ApiRequest request, String apiName) {
        try {
            ApiCallLog logEntry = ApiCallLog.builder()
                    .userId(request.getUserId() != null ? request.getUserId() : "anonymous")
                    .userType(request.getUserType() != null ? request.getUserType() : "UNKNOWN")
                    .level(request.getLevel())
                    .department(request.getDepartment())
                    .apiName(apiName)
                    .build();
            
            apiCallLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to save API call log", e);
        }
    }
}

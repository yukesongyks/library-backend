package com.library.demo.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo.annotation.CallLog;
import com.library.demo.entity.DemoCallLog;
import com.library.demo.mapper.DemoCallLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CallLogAspect {

    private final DemoCallLogMapper callLogMapper;
    private final ObjectMapper objectMapper;

    @Around("@annotation(callLog)")
    public Object logApiCall(ProceedingJoinPoint joinPoint, CallLog callLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        try {
            Object[] args = joinPoint.getArgs();
            String requestParams = objectMapper.writeValueAsString(args);
            String responseData = objectMapper.writeValueAsString(result);

            saveCallLogAsync(
                    callLog.apiType().name(),
                    requestParams,
                    responseData,
                    (int) executionTime
            );
        } catch (Exception e) {
            log.error("Failed to save call log", e);
        }

        return result;
    }

    @Async("callLogExecutor")
    public void saveCallLogAsync(String apiType, String requestParams,
                                  String responseData, int executionTimeMs) {
        try {
            DemoCallLog logEntry = new DemoCallLog();
            logEntry.setApiType(apiType);
            // 兜底方案：从请求头获取模拟用户信息，实际项目中应从 SecurityContext 获取
            logEntry.setCallerId("mock-user-001");
            logEntry.setCallerName("Mock User");
            logEntry.setPersonType("正式");
            logEntry.setPersonLevel("P6");
            logEntry.setDepartment("技术部");
            logEntry.setRequestParams(requestParams);
            logEntry.setResponseData(responseData);
            logEntry.setExecutionTimeMs(executionTimeMs);
            logEntry.setCallTime(LocalDateTime.now());

            callLogMapper.insert(logEntry);
        } catch (Exception e) {
            log.error("Async save call log failed", e);
        }
    }
}

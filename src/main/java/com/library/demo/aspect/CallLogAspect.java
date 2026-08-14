package com.library.demo.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo.annotation.CallLog;
import com.library.demo.service.CallLogAsyncWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CallLogAspect {

    private final CallLogAsyncWriter callLogAsyncWriter;
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

            callLogAsyncWriter.saveCallLogAsync(
                    callLog.apiType().name(),
                    requestParams,
                    responseData,
                    (int) executionTime
            );
        } catch (Exception e) {
            log.error("Failed to serialize call log params, apiType={}", callLog.apiType().name(), e);
        }

        return result;
    }
}

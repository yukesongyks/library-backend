package com.library.demo.service;

import com.library.demo.entity.DemoCallLog;
import com.library.demo.mapper.DemoCallLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 异步埋点写入服务。
 * <p>
 * 独立为单独的 Service，确保 Spring AOP 代理能正确拦截 {@code @Async} 注解，
 * 避免同类内部方法调用导致代理失效的问题。
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CallLogAsyncWriter {

    private static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");

    private final DemoCallLogMapper callLogMapper;

    @Value("${demo.call-log.default-caller-id:mock-user-001}")
    private String defaultCallerId;

    @Value("${demo.call-log.default-caller-name:Mock User}")
    private String defaultCallerName;

    @Value("${demo.call-log.default-person-type:正式}")
    private String defaultPersonType;

    @Value("${demo.call-log.default-person-level:P6}")
    private String defaultPersonLevel;

    @Value("${demo.call-log.default-department:技术部}")
    private String defaultDepartment;

    /**
     * 异步写入调用日志。
     *
     * @param apiType        接口类型
     * @param requestParams  请求参数 JSON
     * @param responseData   响应数据 JSON
     * @param executionTimeMs 执行耗时（毫秒）
     */
    @Async("callLogExecutor")
    public void saveCallLogAsync(String apiType, String requestParams,
                                  String responseData, int executionTimeMs) {
        try {
            DemoCallLog logEntry = new DemoCallLog();
            logEntry.setApiType(apiType);
            // 兜底方案：使用配置项中的默认用户信息，实际项目中应从 SecurityContext 获取
            logEntry.setCallerId(defaultCallerId);
            logEntry.setCallerName(defaultCallerName);
            logEntry.setPersonType(defaultPersonType);
            logEntry.setPersonLevel(defaultPersonLevel);
            logEntry.setDepartment(defaultDepartment);
            logEntry.setRequestParams(requestParams);
            logEntry.setResponseData(responseData);
            logEntry.setExecutionTimeMs(executionTimeMs);
            logEntry.setCallTime(LocalDateTime.now(ZONE_SHANGHAI));

            callLogMapper.insert(logEntry);
        } catch (Exception e) {
            log.error("Async save call log failed, apiType={}", apiType, e);
        }
    }
}

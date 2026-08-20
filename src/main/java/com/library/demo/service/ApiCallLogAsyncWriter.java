package com.library.demo.service;

import com.library.demo.model.entity.ApiCallLog;
import com.library.demo.repository.ApiCallLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 异步写入 API 调用日志的独立 Service Bean。
 * <p>
 * 从 ApiCallLogAspect 中提取出来，确保 Spring AOP 代理能拦截 @Async 注解，
 * 实现真正的异步写入，不阻塞主接口响应。
 * </p>
 */
@Service
public class ApiCallLogAsyncWriter {

    private static final Logger log = LoggerFactory.getLogger(ApiCallLogAsyncWriter.class);
    private static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");

    private final ApiCallLogRepository repository;

    public ApiCallLogAsyncWriter(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    /**
     * 异步保存 API 调用日志。
     * <p>
     * 所有用户信息必须在主线程中提取后作为参数传入，
     * 因为异步线程中无法访问 RequestContextHolder。
     * </p>
     *
     * @param apiType         API 类型标识
     * @param requestPayload  请求体 JSON
     * @param responsePayload 响应体 JSON
     * @param duration        耗时（毫秒）
     * @param userId          用户 ID（从请求头提取）
     * @param userName        用户名（从请求头提取）
     * @param personnelType   人员类型（从请求头提取）
     * @param personnelLevel  人员层级（从请求头提取）
     * @param department      部门（从请求头提取）
     */
    @Async
    public void saveLogAsync(String apiType, String requestPayload, String responsePayload,
                             long duration, String userId, String userName,
                             String personnelType, String personnelLevel, String department) {
        try {
            ApiCallLog logEntry = new ApiCallLog();
            logEntry.setApiType(apiType);
            logEntry.setRequestPayload(truncate(requestPayload, 4000));
            logEntry.setResponsePayload(truncate(responsePayload, 4000));
            logEntry.setDurationMs(duration);
            logEntry.setCreatedAt(LocalDateTime.now(ZONE_SHANGHAI));
            logEntry.setUserId(userId);
            logEntry.setUserName(userName);
            logEntry.setPersonnelType(personnelType);
            logEntry.setPersonnelLevel(personnelLevel);
            logEntry.setDepartment(department);

            repository.save(logEntry);
        } catch (Exception e) {
            log.error("Async save failed for apiType={}: {}", apiType, e.getMessage(), e);
        }
    }

    private String truncate(String value, int maxLen) {
        if (value == null) return null;
        return value.length() > maxLen ? value.substring(0, maxLen) : value;
    }
}

package com.example.library.controller;

import com.example.library.model.ApiCallLog;
import com.example.library.repository.ApiCallLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 报表接口与埋点单元测试。对应 tasks D4/E4。
 */
@SpringBootTest
@AutoConfigureMockMvc
class MetricsAndTrackingTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ApiCallLogRepository apiCallLogRepository;

    // Scenario: 带用户调用被埋点
    @Test
    void callWithUser_isTracked() throws Exception {
        long before = apiCallLogRepository.count();
        mockMvc.perform(get("/api/hello-world").header("X-User-Id", "1"))
                .andExpect(status().isOk());
        long after = apiCallLogRepository.count();
        assert after > before : "埋点未写入";
        List<ApiCallLog> all = apiCallLogRepository.findAll();
        ApiCallLog last = all.get(all.size() - 1);
        assert "helloworld".equals(last.getApiName()) : "apiName 不匹配";
        assert "内部".equals(last.getUserType()) : "userType 应为 内部";
    }

    // Scenario: 匿名调用被埋点
    @Test
    void anonymousCall_isTracked() throws Exception {
        mockMvc.perform(get("/api/hello-world"))
                .andExpect(status().isOk());
        List<ApiCallLog> all = apiCallLogRepository.findAll();
        ApiCallLog last = all.get(all.size() - 1);
        assert last.getUserId() == null : "匿名调用 userId 应为 null";
        assert "unknown".equals(last.getUserType()) : "匿名 userType 应为 unknown";
    }

    // Scenario: 饼图按部门
    @Test
    void summary_pie_department() throws Exception {
        mockMvc.perform(get("/api/metrics/summary")
                        .param("dimension", "department")
                        .param("chartType", "pie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void summary_invalidDimension_returns400() throws Exception {
        mockMvc.perform(get("/api/metrics/summary")
                        .param("dimension", "foo")
                        .param("chartType", "pie"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void summary_invalidChartType_returns400() throws Exception {
        mockMvc.perform(get("/api/metrics/summary")
                        .param("dimension", "department")
                        .param("chartType", "scatter"))
                .andExpect(status().isBadRequest());
    }
}

package com.example.library.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 导出接口单元测试。对应 tasks E4。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Scenario: 导出 helloworld CSV
    @Test
    void exportHelloworldCsv() throws Exception {
        mockMvc.perform(get("/api/export").param("tab", "helloworld").param("format", "csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("helloworld.csv")));
    }

    @Test
    void exportHashJson() throws Exception {
        mockMvc.perform(get("/api/export").param("tab", "hash").param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].algorithm").value("SHA-256"));
    }

    @Test
    void exportInvalidTab_returns400() throws Exception {
        mockMvc.perform(get("/api/export").param("tab", "foo").param("format", "csv"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void exportInvalidFormat_returns400() throws Exception {
        mockMvc.perform(get("/api/export").param("tab", "hash").param("format", "xml"))
                .andExpect(status().isBadRequest());
    }
}

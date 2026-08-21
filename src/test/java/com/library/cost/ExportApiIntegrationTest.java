package com.library.cost;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExportApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exportCsvContainsHeaderAndRows() throws Exception {
        String body = mockMvc.perform(get("/api/cost/export")
                        .param("dimension", "department")
                        .param("year", "2025")
                        .param("format", "csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        assertThat(body).startsWith("名称,");
        assertThat(body).contains("研发部");
        assertThat(body).contains("306000.00");
    }

    @Test
    void exportXlsxReturnsSpreadsheetBytes() throws Exception {
        byte[] bytes = mockMvc.perform(get("/api/cost/export")
                        .param("dimension", "project")
                        .param("year", "2025")
                        .param("format", "xlsx"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .andReturn().getResponse().getContentAsByteArray();

        assertThat(bytes.length).isGreaterThan(1000);
    }
}
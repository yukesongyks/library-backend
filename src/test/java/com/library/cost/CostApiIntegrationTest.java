package com.library.cost;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CostApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void summaryReturnsSeedBasedTotals() throws Exception {
        String body = decode(mockMvc.perform(get("/api/cost/summary").param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                );

        JsonNode data = objectMapper.readTree(body).path("data");
        assertThat(data.path("totalCost").decimalValue()).isEqualByComparingTo("2004000.00");
        assertThat(data.path("laborCost").decimalValue()).isEqualByComparingTo("414000.00");
        assertThat(data.path("projectCost").decimalValue()).isEqualByComparingTo("1590000.00");
        assertThat(data.path("laborRatio").doubleValue()).isEqualTo(20.66);
        assertThat(data.path("projectRatio").doubleValue()).isEqualTo(79.34);
        assertThat(data.path("overBudgetCount").asInt()).isEqualTo(1);
        assertThat(data.path("monthlyTrend")).hasSize(6);
    }

    @Test
    void analysisProjectDimensionShowsOverBudgetProject() throws Exception {
        String body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "project")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                );

        JsonNode records = objectMapper.readTree(body).path("data").path("records");
        assertThat(records).hasSize(4);
        JsonNode dataPlatform = null;
        for (JsonNode r : records) {
            if ("数据中台".equals(r.path("name").asText())) {
                dataPlatform = r;
            }
        }
        assertThat(dataPlatform).isNotNull();
        assertThat(dataPlatform.path("budgetRatio").doubleValue()).isEqualTo(120.00);
        assertThat(dataPlatform.path("overBudgetAmount").decimalValue())
                .isEqualByComparingTo("80000.00");
    }

    @Test
    void analysisDepartmentDimensionGroupsByDepartment() throws Exception {
        String body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "department")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                );

        JsonNode records = objectMapper.readTree(body).path("data").path("records");
        assertThat(records).hasSize(2);
        assertThat(records.get(0).path("name").asText()).isEqualTo("研发部");
        assertThat(records.get(0).path("laborCost").decimalValue())
                .isEqualByComparingTo("306000.00");
        assertThat(records.get(0).path("budgetRatio").doubleValue()).isEqualTo(71.67);
    }

    @Test
    void analysisRejectsInvalidDimension() throws Exception {
        mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "unknown")
                        .param("year", "2025"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void analysisQuarterFilterWorks() throws Exception {
        String body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "quarter")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                );

        JsonNode records = objectMapper.readTree(body).path("data").path("records");
        assertThat(records).hasSize(2);
        assertThat(records.get(0).path("name").asText()).isEqualTo("2025-Q1");
        assertThat(records.get(0).path("projectActual").decimalValue())
                .isEqualByComparingTo("795000.00");
    }

    @Test
    void analysisEmployeeDimensionRoleFilterExcludesOthers() throws Exception {
        String body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "employee")
                        .param("year", "2025")
                        .param("role", "TEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                );

        JsonNode records = objectMapper.readTree(body).path("data").path("records");
        // only 李四 (TEST) has labor rows; 张三/王五/赵六 must be absent
        assertThat(records).hasSize(1);
        assertThat(records.get(0).path("name").asText()).isEqualTo("李四");
        assertThat(records.get(0).path("laborCost").decimalValue())
                .isEqualByComparingTo("90000.00");
    }

    @Test
    @Transactional
    void analysisZeroBudgetProjectRatioGuard() throws Exception {
        // runtime seed extension (rolled back by @Transactional): budget 0 project with actual cost
        jdbcTemplate.update("INSERT INTO project (id, name, code, business_line_id, department_id, budget_amount)"
                + " VALUES (5, '零预算项目', 'ZERO', 1, 1, 0)");
        jdbcTemplate.update("INSERT INTO project_cost (project_id, month, actual_amount)"
                + " VALUES (5, '2025-06', 50000.00)");

        String body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "project")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                );

        JsonNode zero = recordByName(objectMapper.readTree(body).path("data").path("records"), "零预算项目");
        assertThat(zero).isNotNull();
        assertThat(zero.path("projectBudget").decimalValue()).isEqualByComparingTo("0");
        assertThat(zero.path("projectActual").decimalValue()).isEqualByComparingTo("50000.00");
        // 0-budget guard: ratio must be 0.0, not Infinity/NaN
        assertThat(zero.path("budgetRatio").doubleValue()).isEqualTo(0.0);
        // overrun sign stays positive (超支为正)
        assertThat(zero.path("overBudgetAmount").decimalValue()).isEqualByComparingTo("50000.00");
    }

    @Test
    void analysisBudgetDedupedInMonthQuarterYearDims() throws Exception {
        // month dimension: key 2025-01
        String body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "month")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                );
        JsonNode month = recordByName(objectMapper.readTree(body).path("data").path("records"), "2025-01");
        assertThat(month).isNotNull();
        assertThat(month.path("projectBudget").decimalValue()).isEqualByComparingTo("2400000.00");
        assertThat(month.path("projectActual").decimalValue()).isEqualByComparingTo("265000.00");

        // quarter dimension: key 2025-Q1 (budget must be added once per project, not per month)
        body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "quarter")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                );
        JsonNode quarter = recordByName(objectMapper.readTree(body).path("data").path("records"), "2025-Q1");
        assertThat(quarter).isNotNull();
        assertThat(quarter.path("projectBudget").decimalValue()).isEqualByComparingTo("2400000.00");
        assertThat(quarter.path("projectActual").decimalValue()).isEqualByComparingTo("795000.00");

        // year dimension: key 2025 (budget still one per project across all 6 months)
        body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "year")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                );
        JsonNode year = recordByName(objectMapper.readTree(body).path("data").path("records"), "2025");
        assertThat(year).isNotNull();
        assertThat(year.path("projectBudget").decimalValue()).isEqualByComparingTo("2400000.00");
        assertThat(year.path("projectActual").decimalValue()).isEqualByComparingTo("1590000.00");
    }

    @Test
    void analysisYearDimensionReturnsSingleYyyyRecord() throws Exception {
        String body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "year")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                );

        JsonNode data = objectMapper.readTree(body).path("data");
        assertThat(data.path("total").asInt()).isEqualTo(1);
        JsonNode year = data.path("records").get(0);
        assertThat(year.path("name").asText()).isEqualTo("2025");
        assertThat(year.path("laborCost").decimalValue()).isEqualByComparingTo("414000.00");
        assertThat(year.path("projectActual").decimalValue()).isEqualByComparingTo("1590000.00");
    }

    @Test
    void analysisBusinessLineDimensionGroups() throws Exception {
        String body = decode(mockMvc.perform(get("/api/cost/analysis")
                        .param("dimension", "business_line")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                );

        JsonNode records = objectMapper.readTree(body).path("data").path("records");
        assertThat(records).hasSize(2);

        JsonNode fintech = recordByName(records, "金融科技线");
        assertThat(fintech).isNotNull();
        assertThat(fintech.path("laborCost").decimalValue()).isEqualByComparingTo("210000.00");
        assertThat(fintech.path("projectActual").decimalValue()).isEqualByComparingTo("1080000.00");
        assertThat(fintech.path("projectBudget").decimalValue()).isEqualByComparingTo("1400000.00");

        JsonNode digital = recordByName(records, "数字企业线");
        assertThat(digital).isNotNull();
        assertThat(digital.path("laborCost").decimalValue()).isEqualByComparingTo("204000.00");
        assertThat(digital.path("projectActual").decimalValue()).isEqualByComparingTo("510000.00");
        assertThat(digital.path("projectBudget").decimalValue()).isEqualByComparingTo("1000000.00");
    }

    private String decode(ResultActions actions) throws Exception {
        // MockHttpServletResponse defaults to ISO-8859-1; JSON body is UTF-8 → decode bytes explicitly
        return new String(actions.andReturn().getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    private JsonNode recordByName(JsonNode records, String name) {
        for (JsonNode r : records) {
            if (name.equals(r.path("name").asText())) {
                return r;
            }
        }
        return null;
    }
}
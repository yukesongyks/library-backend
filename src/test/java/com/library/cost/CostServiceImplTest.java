package com.library.cost;

import com.library.cost.dto.AnalysisQuery;
import com.library.cost.dto.CostAnalysisItem;
import com.library.cost.dto.CostSummaryDTO;
import com.library.cost.dto.LaborCostRow;
import com.library.cost.dto.ProjectCostRow;
import com.library.cost.entity.Department;
import com.library.cost.entity.Employee;
import com.library.cost.entity.Project;
import com.library.cost.mapper.BusinessLineMapper;
import com.library.cost.mapper.CostReportMapper;
import com.library.cost.mapper.DepartmentMapper;
import com.library.cost.mapper.EmployeeMapper;
import com.library.cost.mapper.ProjectMapper;
import com.library.cost.service.CostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CostServiceImplTest {

    private CostServiceImpl service;
    private CostReportMapper reportMapper;

    @BeforeEach
    void setUp() {
        reportMapper = mock(CostReportMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        BusinessLineMapper businessLineMapper = mock(BusinessLineMapper.class);
        EmployeeMapper employeeMapper = mock(EmployeeMapper.class);
        ProjectMapper projectMapper = mock(ProjectMapper.class);

        when(departmentMapper.selectList(any())).thenReturn(List.of(
                dept(1L, "研发部"), dept(2L, "产品部")));
        when(employeeMapper.selectList(any())).thenReturn(List.of(
                emp(1L, "张三"), emp(2L, "李四")));
        when(projectMapper.selectList(any())).thenReturn(List.of(
                proj(1L, "核心交易系统", 1L, 1L, new BigDecimal("1000000.00"))));

        when(reportMapper.selectLaborRows("2025")).thenReturn(List.of(
                new LaborCostRow(1L, "2025-01", new BigDecimal("20000.00"), "DEV", 1L, 1L, 1L),
                new LaborCostRow(1L, "2025-02", new BigDecimal("20000.00"), "DEV", 1L, 1L, 1L),
                new LaborCostRow(1L, "2025-01", new BigDecimal("15000.00"), "TEST", 1L, 1L, 2L)));
        when(reportMapper.selectProjectCostRows("2025")).thenReturn(List.of(
                new ProjectCostRow(1L, "2025-01", new BigDecimal("50000.00"), 1L, 1L, new BigDecimal("1000000.00")),
                new ProjectCostRow(1L, "2025-02", new BigDecimal("50000.00"), 1L, 1L, new BigDecimal("1000000.00"))));

        service = new CostServiceImpl(reportMapper, departmentMapper, businessLineMapper,
                employeeMapper, projectMapper);
    }

    private Department dept(Long id, String name) {
        Department d = new Department();
        d.setId(id);
        d.setName(name);
        d.setCode("C" + id);
        return d;
    }

    private Employee emp(Long id, String name) {
        Employee e = new Employee();
        e.setId(id);
        e.setName(name);
        e.setEmployeeNo("P" + id);
        return e;
    }

    private Project proj(Long id, String name, Long lineId, Long deptId, BigDecimal budget) {
        Project p = new Project();
        p.setId(id);
        p.setName(name);
        p.setCode("CODE" + id);
        p.setBusinessLineId(lineId);
        p.setDepartmentId(deptId);
        p.setBudgetAmount(budget);
        return p;
    }

    @Test
    void summaryAggregatesTotalsAndTrend() {
        CostSummaryDTO s = service.summary("2025");
        assertThat(s.totalCost()).isEqualByComparingTo("155000.00");
        assertThat(s.laborCost()).isEqualByComparingTo("55000.00");
        assertThat(s.projectCost()).isEqualByComparingTo("100000.00");
        assertThat(s.laborRatio()).isEqualTo(35.48);
        assertThat(s.projectRatio()).isEqualTo(64.52);
        assertThat(s.monthlyTrend()).hasSize(2);
        assertThat(s.monthlyTrend().get(0).month()).isEqualTo("2025-01");
    }

    @Test
    void departmentDimensionGroupsByDepartment() {
        AnalysisQuery query = new AnalysisQuery("department", "2025", null, null, null);
        List<CostAnalysisItem> items = service.queryItems(query);
        assertThat(items).hasSize(1);
        CostAnalysisItem item = items.get(0);
        assertThat(item.name()).isEqualTo("研发部");
        assertThat(item.laborCost()).isEqualByComparingTo("55000.00");
        assertThat(item.projectBudget()).isEqualByComparingTo("1000000.00");
        assertThat(item.projectActual()).isEqualByComparingTo("100000.00");
        assertThat(item.budgetRatio()).isEqualTo(10.00);
    }

    @Test
    void roleFilterRestrictsLaborRows() {
        AnalysisQuery query = new AnalysisQuery("employee", "2025", null, null, "DEV");
        List<CostAnalysisItem> items = service.queryItems(query);
        assertThat(items).hasSize(1);
        assertThat(items.get(0).laborCost()).isEqualByComparingTo("40000.00");
    }

    @Test
    void invalidDimensionRejected() {
        AnalysisQuery query = new AnalysisQuery("bad", "2025", null, null, null);
        try {
            service.queryItems(query);
            assertThat(false).isTrue();
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("不支持的统计维度");
        }
    }
}

package com.library.cost.service;

import com.library.cost.dto.AnalysisQuery;
import com.library.cost.dto.AnalysisResponse;
import com.library.cost.dto.CostAnalysisItem;
import com.library.cost.dto.CostSummaryDTO;
import com.library.cost.dto.LaborCostRow;
import com.library.cost.dto.MonthlyTrendItem;
import com.library.cost.dto.ProjectCostRow;
import com.library.cost.entity.Project;
import com.library.cost.mapper.BusinessLineMapper;
import com.library.cost.mapper.CostReportMapper;
import com.library.cost.mapper.DepartmentMapper;
import com.library.cost.mapper.EmployeeMapper;
import com.library.cost.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class CostServiceImpl implements CostService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final Set<String> DIMENSIONS = Set.of(
            "department", "project", "business_line", "employee", "month", "quarter", "year");
    private static final Set<String> ROLES = Set.of("DEV", "TEST", "PM", "OPS");

    private final CostReportMapper costReportMapper;
    private final DepartmentMapper departmentMapper;
    private final BusinessLineMapper businessLineMapper;
    private final EmployeeMapper employeeMapper;
    private final ProjectMapper projectMapper;

    public CostServiceImpl(CostReportMapper costReportMapper, DepartmentMapper departmentMapper,
                           BusinessLineMapper businessLineMapper, EmployeeMapper employeeMapper,
                           ProjectMapper projectMapper) {
        this.costReportMapper = costReportMapper;
        this.departmentMapper = departmentMapper;
        this.businessLineMapper = businessLineMapper;
        this.employeeMapper = employeeMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    public CostSummaryDTO summary(String year) {
        String resolvedYear = resolveYear(year);
        List<LaborCostRow> laborRows = costReportMapper.selectLaborRows(resolvedYear);
        List<ProjectCostRow> costRows = costReportMapper.selectProjectCostRows(resolvedYear);

        BigDecimal laborCost = sumAmounts(laborRows.stream().map(LaborCostRow::amount).toList());
        BigDecimal projectCost = sumAmounts(costRows.stream().map(ProjectCostRow::actualAmount).toList());
        BigDecimal totalCost = laborCost.add(projectCost);

        Set<String> months = new TreeSet<>();
        laborRows.forEach(r -> months.add(r.month()));
        costRows.forEach(r -> months.add(r.month()));
        List<MonthlyTrendItem> trend = new ArrayList<>();
        for (String month : months) {
            BigDecimal ml = sumAmounts(laborRows.stream()
                    .filter(r -> r.month().equals(month)).map(LaborCostRow::amount).toList());
            BigDecimal mp = sumAmounts(costRows.stream()
                    .filter(r -> r.month().equals(month)).map(ProjectCostRow::actualAmount).toList());
            trend.add(new MonthlyTrendItem(month, ml, mp, ml.add(mp)));
        }

        Map<Long, ProjectTotals> byProject = buildProjectTotals(costRows);
        long overBudget = byProject.values().stream()
                .filter(t -> t.actual.compareTo(t.budget) > 0)
                .count();

        return new CostSummaryDTO(
                totalCost, laborCost, projectCost,
                ratio(laborCost, totalCost), ratio(projectCost, totalCost),
                (int) overBudget, trend);
    }

    @Override
    public AnalysisResponse analysis(AnalysisQuery query) {
        List<CostAnalysisItem> items = queryItems(query);
        return new AnalysisResponse(items, items.size());
    }

    @Override
    public List<CostAnalysisItem> queryItems(AnalysisQuery query) {
        String dimension = requireDimension(query.getDimension());
        String resolvedYear = resolveYear(query.getYear());
        String month = blankToNull(query.getMonth());
        String quarter = blankToNull(query.getQuarter());
        String role = blankToNull(query.getRole());
        validatePeriod(month, quarter);
        if (role != null && !ROLES.contains(role.toUpperCase())) {
            throw new IllegalArgumentException("不支持的岗位角色: " + role);
        }

        List<LaborCostRow> laborRows = costReportMapper.selectLaborRows(resolvedYear).stream()
                .filter(r -> matchesPeriod(r.month(), month, quarter))
                .filter(r -> role == null || r.role().equalsIgnoreCase(role))
                .toList();
        List<ProjectCostRow> costRows = costReportMapper.selectProjectCostRows(resolvedYear).stream()
                .filter(r -> matchesPeriod(r.month(), month, quarter))
                .toList();

        Map<Long, Project> projectsById = projectMapper.selectList(null).stream()
                .collect(Collectors.toMap(Project::getId, p -> p));

        Map<String, BigDecimal> laborByKey = new LinkedHashMap<>();
        for (LaborCostRow r : laborRows) {
            laborByKey.merge(laborDimKey(r, dimension, resolvedYear), r.amount(), BigDecimal::add);
        }

        // 员工 → 参与项目 映射（用于 employee 维度与角色过滤联动）
        Map<Long, Set<Long>> projectEmployeeIds = new HashMap<>();
        for (LaborCostRow r : laborRows) {
            projectEmployeeIds.computeIfAbsent(r.projectId(), k -> new HashSet<>()).add(r.employeeId());
        }

        Map<String, CostAgg> aggByKey = new LinkedHashMap<>();
        Set<String> projectBudgetKeys = new HashSet<>();
        for (ProjectCostRow row : costRows) {
            Project project = projectsById.get(row.projectId());
            if (project == null) {
                continue;
            }
            List<String> keys = isTimeDimension(dimension)
                    ? List.of(timeKey(row.month(), dimension, resolvedYear))
                    : entityDimKeys(project, dimension, projectEmployeeIds.getOrDefault(row.projectId(), Set.of()));
            for (String key : keys) {
                CostAgg agg = aggByKey.computeIfAbsent(key, k -> new CostAgg());
                agg.projectActual = agg.projectActual.add(row.actualAmount());
                String budgetKey = key + "#" + row.projectId();
                if (projectBudgetKeys.add(budgetKey)) {
                    agg.projectBudget = agg.projectBudget.add(row.budgetAmount());
                }
            }
        }
        for (Map.Entry<String, BigDecimal> e : laborByKey.entrySet()) {
            aggByKey.computeIfAbsent(e.getKey(), k -> new CostAgg()).laborCost = e.getValue();
        }

        Map<String, String> names = dimNames(dimension, projectsById);
        List<CostAnalysisItem> items = new ArrayList<>();
        for (Map.Entry<String, CostAgg> e : aggByKey.entrySet()) {
            CostAgg a = e.getValue();
            String label = isTimeDimension(dimension) ? e.getKey() : names.getOrDefault(e.getKey(), e.getKey());
            items.add(new CostAnalysisItem(
                    label,
                    a.laborCost,
                    a.projectBudget,
                    a.projectActual,
                    ratio(a.projectActual, a.projectBudget),
                    a.projectActual.subtract(a.projectBudget)));
        }
        items.sort(Comparator.comparing(CostAnalysisItem::laborCost).reversed()
                .thenComparing(CostAnalysisItem::name));
        return items;
    }

    // ---------- helpers ----------

    private String requireDimension(String dimension) {
        if (dimension == null || !DIMENSIONS.contains(dimension)) {
            throw new IllegalArgumentException("不支持的统计维度: " + dimension);
        }
        return dimension;
    }

    private String resolveYear(String year) {
        String y = blankToNull(year);
        if (y == null) {
            return String.valueOf(LocalDate.now().getYear());
        }
        if (!y.matches("\\d{4}")) {
            throw new IllegalArgumentException("年份格式应为 YYYY: " + year);
        }
        return y;
    }

    private void validatePeriod(String month, String quarter) {
        if (month != null && !month.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new IllegalArgumentException("月份格式应为 YYYY-MM: " + month);
        }
        if (quarter != null && !quarter.matches("Q[1-4]")) {
            throw new IllegalArgumentException("季度格式应为 Q1-Q4: " + quarter);
        }
    }

    private boolean matchesPeriod(String rowMonth, String month, String quarter) {
        if (month != null && !rowMonth.equals(month)) {
            return false;
        }
        return quarter == null || quarterOf(rowMonth).equals(quarter);
    }

    private boolean isTimeDimension(String dimension) {
        return "month".equals(dimension) || "quarter".equals(dimension) || "year".equals(dimension);
    }

    private String timeKey(String month, String dimension, String year) {
        return switch (dimension) {
            case "month" -> month;
            case "quarter" -> year + "-" + quarterOf(month);
            case "year" -> year;
            default -> "";
        };
    }

    private String laborDimKey(LaborCostRow row, String dimension, String year) {
        return switch (dimension) {
            case "department" -> String.valueOf(row.departmentId());
            case "project" -> String.valueOf(row.projectId());
            case "business_line" -> String.valueOf(row.businessLineId());
            case "employee" -> String.valueOf(row.employeeId());
            case "month" -> row.month();
            case "quarter" -> year + "-" + quarterOf(row.month());
            case "year" -> year;
            default -> throw new IllegalArgumentException("不支持的统计维度: " + dimension);
        };
    }

    private List<String> entityDimKeys(Project project, String dimension, Set<Long> employeeIds) {
        return switch (dimension) {
            case "department" -> List.of(String.valueOf(project.getDepartmentId()));
            case "project" -> List.of(String.valueOf(project.getId()));
            case "business_line" -> List.of(String.valueOf(project.getBusinessLineId()));
            case "employee" -> employeeIds.stream().map(String::valueOf).distinct().toList();
            default -> List.of();
        };
    }

    private Map<String, String> dimNames(String dimension, Map<Long, Project> projectsById) {
        Map<String, String> names = new HashMap<>();
        switch (dimension) {
            case "department" -> departmentMapper.selectList(null)
                    .forEach(d -> names.put(String.valueOf(d.getId()), d.getName()));
            case "project" -> projectsById.forEach((id, p) -> names.put(String.valueOf(id), p.getName()));
            case "business_line" -> businessLineMapper.selectList(null)
                    .forEach(b -> names.put(String.valueOf(b.getId()), b.getName()));
            case "employee" -> employeeMapper.selectList(null)
                    .forEach(e -> names.put(String.valueOf(e.getId()), e.getName()));
            default -> { }
        }
        return names;
    }

    private Map<Long, ProjectTotals> buildProjectTotals(List<ProjectCostRow> rows) {
        Map<Long, ProjectTotals> map = new LinkedHashMap<>();
        for (ProjectCostRow row : rows) {
            ProjectTotals t = map.computeIfAbsent(row.projectId(), k -> new ProjectTotals());
            t.actual = t.actual.add(row.actualAmount());
            t.budget = row.budgetAmount();
        }
        return map;
    }

    private BigDecimal sumAmounts(List<BigDecimal> amounts) {
        return amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double ratio(BigDecimal part, BigDecimal whole) {
        if (whole == null || whole.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return part.multiply(HUNDRED).divide(whole, 2, RoundingMode.HALF_UP).doubleValue();
    }

    private String quarterOf(String month) {
        int m = Integer.parseInt(month.substring(5, 7));
        return "Q" + ((m - 1) / 3 + 1);
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static class CostAgg {
        BigDecimal laborCost = BigDecimal.ZERO;
        BigDecimal projectBudget = BigDecimal.ZERO;
        BigDecimal projectActual = BigDecimal.ZERO;
    }

    private static class ProjectTotals {
        BigDecimal actual = BigDecimal.ZERO;
        BigDecimal budget = BigDecimal.ZERO;
    }
}

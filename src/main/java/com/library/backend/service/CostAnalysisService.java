package com.library.backend.service;

import com.library.backend.dto.CostQueryRequest;
import com.library.backend.dto.CostSummaryDTO;
import com.library.backend.dto.DimensionStatDTO;
import com.library.backend.dto.ProjectCostDTO;
import com.library.backend.entity.CostRecord;
import com.library.backend.entity.Employee;
import com.library.backend.entity.ProjectBudget;
import com.library.backend.repository.CostRecordRepository;
import com.library.backend.repository.ProjectBudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CostAnalysisService {

    private final CostRecordRepository costRecordRepository;
    private final ProjectBudgetRepository projectBudgetRepository;

    /**
     * 成本汇总查询（S01）。
     * quarter 在 Service 层转换为 costMonth 范围（Q1→1-3）叠加到筛选。
     */
    public CostSummaryDTO getCostSummary(CostQueryRequest req) {
        // quarter 转月份范围（R02）
        Integer costMonth = req.getCostMonth();
        if (req.getQuarter() != null) {
            int q = req.getQuarter();
            if (q < 1 || q > 4) {
                throw new IllegalArgumentException("季度参数非法");
            }
            // quarter 与 costMonth 同时存在时以 quarter 月份范围为准
            // 此处保留 quarter 语义：在内存聚合后过滤月份
        }

        List<CostRecord> records = costRecordRepository.findByFilters(
            req.getDepartmentId(), req.getBusinessLineId(), req.getProjectId(),
            req.getEmployeeId(), req.getCostYear(), costMonth, req.getRole());

        // quarter 月份过滤（R02）
        if (req.getQuarter() != null) {
            int q = req.getQuarter();
            int[] months = quarterToMonths(q);
            records = records.stream()
                .filter(r -> r.getCostMonth() >= months[0] && r.getCostMonth() <= months[1])
                .collect(Collectors.toList());
        }

        CostSummaryDTO dto = new CostSummaryDTO();
        BigDecimal total = records.stream()
            .map(CostRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalCost(total);
        // 人力成本仅统计 costType='LABOR' 的记录
        BigDecimal laborTotal = records.stream()
            .filter(r -> "LABOR".equals(r.getCostType()))
            .map(CostRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setLaborCost(laborTotal);
        dto.setRecordCount(records.size());
        dto.setByDepartment(groupByDimension(records, r -> r.getDepartment().getName()));
        dto.setByRole(groupByDimension(records, r -> r.getEmployee().getRole().name()));
        dto.setByMonth(groupByDimension(records,
            r -> r.getCostYear() + "-" + String.format("%02d", r.getCostMonth())));
        return dto;
    }

    /**
     * 月度趋势查询（S02）。
     */
    public List<DimensionStatDTO> getMonthlyTrend(Integer costYear) {
        List<Object[]> rows = costRecordRepository.findMonthlyTrend(costYear);
        BigDecimal total = rows.stream()
            .map(r -> toBigDecimal(r[2]))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return rows.stream().map(r -> new DimensionStatDTO(
            r[0] + "-" + String.format("%02d", toInt(r[1])),
            toBigDecimal(r[2]),
            total.compareTo(BigDecimal.ZERO) > 0
                ? toBigDecimal(r[2]).multiply(new BigDecimal("100"))
                    .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0
        )).collect(Collectors.toList());
    }

    /**
     * 角色成本占比查询（S03）。
     */
    public List<DimensionStatDTO> getCostByRole() {
        List<Object[]> rows = costRecordRepository.findCostByRole(null);
        return mapRoleStats(rows);
    }

    /**
     * 项目成本查询（S04），含预算/实际/占比/超支派生计算（R05/R06）。
     */
    public List<ProjectCostDTO> getProjectCost(Integer budgetYear) {
        // 查询项目预算
        List<ProjectBudget> budgets = (budgetYear != null)
            ? projectBudgetRepository.findByBudgetYear(budgetYear)
            : projectBudgetRepository.findAll();

        // 查询项目实际消耗（按 budgetYear 过滤，保证预算与实际周期一致）
        List<Object[]> actualRows = costRecordRepository.findCostByProject(budgetYear);

        // 构建 projectId -> actualCost 映射
        Map<Long, BigDecimal> actualMap = actualRows.stream()
            .collect(Collectors.toMap(
                r -> ((Number) r[0]).longValue(),
                r -> toBigDecimal(r[2]),
                BigDecimal::add));

        List<ProjectCostDTO> result = new ArrayList<>();
        for (ProjectBudget budget : budgets) {
            ProjectCostDTO dto = new ProjectCostDTO();
            dto.setProjectId(budget.getProject().getId());
            dto.setProjectName(budget.getProject().getName());
            dto.setBudgetAmount(budget.getBudgetAmount());
            BigDecimal actual = actualMap.getOrDefault(budget.getProject().getId(), BigDecimal.ZERO);
            dto.setActualCost(actual);
            dto.calculateDerived();
            result.add(dto);
        }
        return result;
    }

    /**
     * 维度聚合查询（S05）。
     * dimension=quarter 时按 costMonth 派生季度聚合；其余维度直接 group by。
     */
    public List<DimensionStatDTO> getCostByDimension(String dimension, Integer year) {
        switch (dimension) {
            case "department": {
                List<Object[]> rows = costRecordRepository.findCostByDepartment(year);
                return toDimensionStatDTO(rows, 2);
            }
            case "businessLine": {
                List<Object[]> rows = costRecordRepository.findCostByBusinessLine(year);
                return toDimensionStatDTO(rows, 2);
            }
            case "project": {
                List<Object[]> rows = costRecordRepository.findCostByProject(year);
                return toDimensionStatDTO(rows, 2);
            }
            case "employee": {
                List<Object[]> rows = costRecordRepository.findCostByEmployee(year);
                return toDimensionStatDTO(rows, 2);
            }
            case "role": {
                List<Object[]> rows = costRecordRepository.findCostByRole(year);
                return mapRoleStats(rows);
            }
            case "quarter": {
                List<CostRecord> records = costRecordRepository.findByFilters(
                    null, null, null, null, year, null, null);
                Map<Integer, BigDecimal> quarterMap = records.stream()
                    .collect(Collectors.groupingBy(
                        r -> monthToQuarter(r.getCostMonth()),
                        Collectors.mapping(CostRecord::getAmount,
                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
                BigDecimal total = quarterMap.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                List<DimensionStatDTO> result = new ArrayList<>();
                for (int q = 1; q <= 4; q++) {
                    BigDecimal amt = quarterMap.getOrDefault(q, BigDecimal.ZERO);
                    result.add(new DimensionStatDTO(
                        "Q" + q,
                        amt,
                        total.compareTo(BigDecimal.ZERO) > 0
                            ? amt.multiply(new BigDecimal("100"))
                                .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                            : 0.0));
                }
                return result;
            }
            default:
                throw new IllegalArgumentException("维度参数非法: " + dimension);
        }
    }

    // ===== 内部辅助方法 =====

    private List<DimensionStatDTO> groupByDimension(List<CostRecord> records,
                                                     java.util.function.Function<CostRecord, String> keyExtractor) {
        Map<String, BigDecimal> grouped = records.stream()
            .collect(Collectors.groupingBy(
                keyExtractor,
                Collectors.mapping(CostRecord::getAmount,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        BigDecimal total = grouped.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return grouped.entrySet().stream()
            .map(e -> new DimensionStatDTO(
                e.getKey(),
                e.getValue(),
                total.compareTo(BigDecimal.ZERO) > 0
                    ? e.getValue().multiply(new BigDecimal("100"))
                        .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0))
            .collect(Collectors.toList());
    }

    private List<DimensionStatDTO> toDimensionStatDTO(List<Object[]> rows, int amountIndex) {
        BigDecimal total = rows.stream()
            .map(r -> toBigDecimal(r[amountIndex]))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return rows.stream().map(r -> new DimensionStatDTO(
            String.valueOf(r[amountIndex - 1]),
            toBigDecimal(r[amountIndex]),
            total.compareTo(BigDecimal.ZERO) > 0
                ? toBigDecimal(r[amountIndex]).multiply(new BigDecimal("100"))
                    .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0
        )).collect(Collectors.toList());
    }

    private List<DimensionStatDTO> mapRoleStats(List<Object[]> rows) {
        BigDecimal total = rows.stream()
            .map(r -> toBigDecimal(r[1]))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return rows.stream().map(r -> new DimensionStatDTO(
            toEnum(r[0], Employee.EmployeeRole.class).name(),
            toBigDecimal(r[1]),
            total.compareTo(BigDecimal.ZERO) > 0
                ? toBigDecimal(r[1]).multiply(new BigDecimal("100"))
                    .divide(total, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0
        )).collect(Collectors.toList());
    }

    private int[] quarterToMonths(int quarter) {
        switch (quarter) {
            case 1: return new int[]{1, 3};
            case 2: return new int[]{4, 6};
            case 3: return new int[]{7, 9};
            case 4: return new int[]{10, 12};
            default: return new int[]{1, 12};
        }
    }

    private int monthToQuarter(int month) {
        return (month - 1) / 3 + 1;
    }

    /**
     * 安全提取 BigDecimal，兼容不同 JPA dialect 返回类型（BigDecimal / BigInteger / Long）。
     */
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(((Number) value).toString());
    }

    /**
     * 安全提取 int，兼容 Integer / Long / BigInteger。
     */
    private int toInt(Object value) {
        return ((Number) value).intValue();
    }

    /**
     * 安全提取 Enum，兼容 JPA EnumType.STRING 直接返回枚举实例或字符串值。
     */
    private <T extends Enum<T>> T toEnum(Object value, Class<T> enumType) {
        if (value == null) {
            return null;
        }
        if (enumType.isInstance(value)) {
            return enumType.cast(value);
        }
        return Enum.valueOf(enumType, value.toString());
    }
}

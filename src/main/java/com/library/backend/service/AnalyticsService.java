package com.library.backend.service;

import com.library.backend.repository.CallLogRepository;
import com.library.backend.repository.CallLogRepository.DimensionCount;
import com.library.backend.repository.CallLogRepository.TrendCount;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分析服务。
 *
 * <p>按维度（personnelType/personnelLevel/department）+ 图表类型（line/pie/bar）
 * 聚合 call_logs 记录（spec tracking-analytics.md）：
 * <ul>
 *   <li>bar / pie → {@code List<{label, value}>}</li>
 *   <li>line → {@code List<{date, values:[{label, value}]}>}</li>
 * </ul>
 * 时间范围为可选参数，缺失时默认取近一年范围（保证空数据返回而非全表扫描误差）。</p>
 */
@Service
public class AnalyticsService {

    public static final Set<String> DIMENSIONS = Set.of("personnelType", "personnelLevel", "department");
    public static final Set<String> CHART_TYPES = Set.of("line", "pie", "bar");

    private final CallLogRepository callLogRepository;

    public AnalyticsService(CallLogRepository callLogRepository) {
        this.callLogRepository = callLogRepository;
    }

    /**
     * bar / pie 聚合。
     */
    public List<Map<String, Object>> aggregateByDimension(String dimension, LocalDateTime start, LocalDateTime end) {
        List<DimensionCount> rows = switch (dimension) {
            case "personnelType" -> callLogRepository.aggregateByPersonnelType(start, end);
            case "personnelLevel" -> callLogRepository.aggregateByPersonnelLevel(start, end);
            case "department" -> callLogRepository.aggregateByDepartment(start, end);
            default -> List.of();
        };
        List<Map<String, Object>> data = new ArrayList<>();
        for (DimensionCount row : rows) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("label", row.getLabel());
            point.put("value", row.getValue());
            data.add(point);
        }
        return data;
    }

    /**
     * line 聚合：按天 + 维度分组。
     *
     * <p>Repository 返回 {@code [{date, label, count}]}，此处按 date 分组聚合成
     * {@code [{date, values:[{label, value}]}]}（spec 折线图数据结构）。</p>
     */
    public List<Map<String, Object>> trendByDimension(String dimension, LocalDateTime start, LocalDateTime end) {
        List<TrendCount> rows = switch (dimension) {
            case "personnelType" -> callLogRepository.trendByPersonnelType(start, end);
            case "personnelLevel" -> callLogRepository.trendByPersonnelLevel(start, end);
            case "department" -> callLogRepository.trendByDepartment(start, end);
            default -> List.of();
        };

        Map<LocalDate, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        for (TrendCount row : rows) {
            LocalDate date = row.getDate();
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("label", row.getLabel());
            point.put("value", row.getValue());
            grouped.computeIfAbsent(date, k -> new ArrayList<>()).add(point);
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Map<String, Object>>> entry : grouped.entrySet()) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", entry.getKey().toString());
            point.put("values", entry.getValue());
            data.add(point);
        }
        return data;
    }
}

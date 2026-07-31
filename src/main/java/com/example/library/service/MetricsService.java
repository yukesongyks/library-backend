package com.example.library.service;

import com.example.library.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报表聚合服务：line 按日聚合；pie/bar 按维度取值聚合。
 * 对应 design.md 3.1 / tasks E3。
 */
@Service
public class MetricsService {

    private static final List<String> VALID_DIMENSIONS =
            Arrays.asList("userType", "userLevel", "department", "apiName");
    private static final List<String> VALID_CHART_TYPES =
            Arrays.asList("line", "pie", "bar");

    private final ApiCallLogRepository apiCallLogRepository;

    public MetricsService(ApiCallLogRepository apiCallLogRepository) {
        this.apiCallLogRepository = apiCallLogRepository;
    }

    public List<Map<String, Object>> summary(String dimension, String chartType) {
        validateDimension(dimension);
        validateChartType(chartType);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(30);

        List<Object[]> rows;
        if ("line".equals(chartType)) {
            rows = apiCallLogRepository.aggregateByDay(start, end);
        } else {
            rows = aggregateByDimension(dimension, start, end);
        }
        return toLabelValueList(rows);
    }

    private List<Object[]> aggregateByDimension(String dimension, LocalDateTime start, LocalDateTime end) {
        switch (dimension) {
            case "userType":
                return apiCallLogRepository.aggregateByUserType(start, end);
            case "userLevel":
                return apiCallLogRepository.aggregateByUserLevel(start, end);
            case "department":
                return apiCallLogRepository.aggregateByDepartment(start, end);
            case "apiName":
                return apiCallLogRepository.aggregateByApiName(start, end);
            default:
                throw new IllegalArgumentException("不支持的 dimension: " + dimension);
        }
    }

    private List<Map<String, Object>> toLabelValueList(List<Object[]> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            String label = String.valueOf(row[0]);
            Long value = ((Number) row[1]).longValue();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", label);
            item.put("value", value);
            result.add(item);
        }
        return result;
    }

    private void validateDimension(String dimension) {
        if (!VALID_DIMENSIONS.contains(dimension)) {
            throw new IllegalArgumentException("不支持的 dimension: " + dimension + "，支持: " + VALID_DIMENSIONS);
        }
    }

    private void validateChartType(String chartType) {
        if (!VALID_CHART_TYPES.contains(chartType)) {
            throw new IllegalArgumentException("不支持的 chartType: " + chartType + "，支持: " + VALID_CHART_TYPES);
        }
    }
}

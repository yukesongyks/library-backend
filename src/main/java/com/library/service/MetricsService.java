package com.library.service;

import com.library.mapper.ApiMetricsMapper;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MetricsService {

    private static final Set<String> VALID_DIMENSIONS = Set.of("caller_type", "caller_level", "caller_dept");
    private final ApiMetricsMapper apiMetricsMapper;

    public MetricsService(ApiMetricsMapper apiMetricsMapper) {
        this.apiMetricsMapper = apiMetricsMapper;
    }

    public Map<String, Object> query(String dimension, String startDate, String endDate, String apiPath) {
        String dim = (dimension != null && VALID_DIMENSIONS.contains(dimension)) ? dimension : "caller_type";

        StringBuilder where = new StringBuilder();
        if (startDate != null && !startDate.isEmpty()) {
            where.append(" AND call_time >= '").append(startDate).append(" 00:00:00'");
        }
        if (endDate != null && !endDate.isEmpty()) {
            where.append(" AND call_time <= '").append(endDate).append(" 23:59:59'");
        }
        if (apiPath != null && !apiPath.isEmpty()) {
            where.append(" AND api_path = '").append(apiPath).append("'");
        }
        String whereCondition = where.toString();

        Long total = apiMetricsMapper.selectTotalCount(whereCondition);
        List<Map<String, Object>> breakdown = apiMetricsMapper.selectStatsByDimension(dim, whereCondition);
        List<Map<String, Object>> trend = apiMetricsMapper.selectTrend(whereCondition);

        Map<String, Object> result = new HashMap<>();
        result.put("dimension", dim);
        result.put("total", total != null ? total : 0L);
        result.put("breakdown", breakdown != null ? breakdown : Collections.emptyList());
        result.put("trend", trend != null ? trend : Collections.emptyList());
        return result;
    }
}
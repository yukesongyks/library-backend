package com.library.service;

import com.library.mapper.ApiMetricsMapper;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MetricsService {

    private static final Set<String> VALID_DIMENSIONS = Set.of("caller_type", "caller_level", "caller_dept");
    private static final Set<String> VALID_API_PATHS = Set.of(
            "/api/helloworld/helloworld",
            "/api/hash/compute",
            "/api/bubblesort/sort",
            "/api/export/export"
    );
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private final ApiMetricsMapper apiMetricsMapper;

    public MetricsService(ApiMetricsMapper apiMetricsMapper) {
        this.apiMetricsMapper = apiMetricsMapper;
    }

    public Map<String, Object> query(String dimension, String startDate, String endDate, String apiPath) {
        String dim = (dimension != null && VALID_DIMENSIONS.contains(dimension)) ? dimension : "caller_type";

        // 参数化绑定：日期格式校验 + apiPath 白名单校验
        String safeStartDate = validateDate(startDate);
        String safeEndDate = validateDate(endDate);
        String safeApiPath = validateApiPath(apiPath);

        Long total = apiMetricsMapper.selectTotalCount(safeStartDate, safeEndDate, safeApiPath);
        List<Map<String, Object>> breakdown = apiMetricsMapper.selectStatsByDimension(dim, safeStartDate, safeEndDate, safeApiPath);
        List<Map<String, Object>> trend = apiMetricsMapper.selectTrend(safeStartDate, safeEndDate, safeApiPath);

        Map<String, Object> result = new HashMap<>();
        result.put("dimension", dim);
        result.put("total", total != null ? total : 0L);
        result.put("breakdown", breakdown != null ? breakdown : Collections.emptyList());
        result.put("trend", trend != null ? trend : Collections.emptyList());
        return result;
    }

    private String validateDate(String date) {
        if (date == null || date.isEmpty()) return null;
        if (!DATE_PATTERN.matcher(date).matches()) {
            throw new IllegalArgumentException("日期格式无效，需为 yyyy-MM-dd: " + date);
        }
        return date;
    }

    private String validateApiPath(String apiPath) {
        if (apiPath == null || apiPath.isEmpty()) return null;
        if (!VALID_API_PATHS.contains(apiPath)) {
            throw new IllegalArgumentException("无效的 apiPath: " + apiPath);
        }
        return apiPath;
    }
}
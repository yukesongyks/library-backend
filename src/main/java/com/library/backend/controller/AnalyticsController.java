package com.library.backend.controller;

import com.library.backend.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分析接口控制器。
 *
 * <p>spec tracking-analytics.md：
 * <ul>
 *   <li>{@code GET /api/analytics/calls?dimension=&chartType=&startTime=&endTime=}</li>
 *   <li>bar / pie → {@code {chartType, dimension, data:[{label, value}]}}</li>
 *   <li>line → {@code {chartType, dimension, data:[{date, values:[{label, value}]}]}]}</li>
 *   <li>无效 dimension → 400 {@code {"error":"Invalid dimension: <v>. Supported: personnelType, personnelLevel, department"}}</li>
 *   <li>无效 chartType → 400 {@code {"error":"Invalid chartType: <v>. Supported: line, pie, bar"}}</li>
 *   <li>无数据 → 200 {@code data: []}</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/calls")
    public Map<String, Object> getCalls(
            @RequestParam("dimension") String dimension,
            @RequestParam("chartType") String chartType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {

        if (!AnalyticsService.DIMENSIONS.contains(dimension)) {
            throw new IllegalArgumentException(
                    "Invalid dimension: " + dimension + ". Supported: personnelType, personnelLevel, department");
        }
        if (!AnalyticsService.CHART_TYPES.contains(chartType)) {
            throw new IllegalArgumentException(
                    "Invalid chartType: " + chartType + ". Supported: line, pie, bar");
        }

        LocalDateTime start = startTime == null || startTime.isBlank()
                ? LocalDateTime.now().minusYears(1) : LocalDateTime.parse(startTime);
        LocalDateTime end = endTime == null || endTime.isBlank()
                ? LocalDateTime.now() : LocalDateTime.parse(endTime);

        List<Map<String, Object>> data;
        if ("line".equals(chartType)) {
            data = analyticsService.trendByDimension(dimension, start, end);
        } else {
            data = analyticsService.aggregateByDimension(dimension, start, end);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("chartType", chartType);
        response.put("dimension", dimension);
        response.put("data", data);
        return response;
    }
}

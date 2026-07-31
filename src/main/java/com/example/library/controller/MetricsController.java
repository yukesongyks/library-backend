package com.example.library.controller;

import com.example.library.service.MetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 报表接口。
 * GET /api/metrics/summary?dimension=&chartType=，返回 [{label, value}]。
 * 对应 tasks E2 / spec Feature: 可视化报表。
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/summary")
    public List<Map<String, Object>> summary(
            @RequestParam String dimension,
            @RequestParam String chartType) {
        return metricsService.summary(dimension, chartType);
    }
}

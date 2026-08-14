package com.library.controller;

import com.library.common.Result;
import com.library.service.MetricsService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics")
    public Result<Map<String, Object>> metrics(
            @RequestParam(required = false) String dimension,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String apiPath) {
        Map<String, Object> data = metricsService.query(dimension, startDate, endDate, apiPath);
        return Result.ok(data);
    }
}
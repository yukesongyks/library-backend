package com.library.demo.controller;

import com.library.demo.model.response.AnalyticsResponse;
import com.library.demo.model.response.ApiResponse;
import com.library.demo.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics")
    public ApiResponse<AnalyticsResponse> getAnalytics(
            @RequestParam(required = false) String dimension,
            @RequestParam(required = false) String apiType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String chartType) {
        return ApiResponse.success(analyticsService.getAnalytics(dimension, apiType, startDate, endDate));
    }
}

package com.library.demo.controller;

import com.library.demo.dto.request.AnalyticsQuery;
import com.library.demo.dto.response.AnalyticsSummary;
import com.library.demo.dto.response.AnalyticsTrend;
import com.library.demo.dto.response.DemoResponse;
import com.library.demo.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public DemoResponse<AnalyticsSummary> summary(AnalyticsQuery query) {
        return DemoResponse.success(analyticsService.getSummary(query));
    }

    @GetMapping("/trend")
    public DemoResponse<AnalyticsTrend> trend(AnalyticsQuery query) {
        return DemoResponse.success(analyticsService.getTrend(query));
    }
}

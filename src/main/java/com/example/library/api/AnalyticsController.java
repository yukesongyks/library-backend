package com.example.library.api;

import com.example.library.domain.AlgorithmType;
import com.example.library.service.AnalyticsService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/report")
    public AnalyticsDtos.AnalyticsReport report(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) AlgorithmType algorithmType,
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String userLevel,
            @RequestParam(required = false) String departmentId) {
        return analyticsService.report(new AnalyticsDtos.AnalyticsFilter(from, to, algorithmType,
                userType, userLevel, departmentId));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "analytics") String scope,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) AlgorithmType algorithmType,
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String userLevel,
            @RequestParam(required = false) String departmentId) {
        if (!"analytics".equalsIgnoreCase(scope) && !"algorithm".equalsIgnoreCase(scope)) {
            throw new IllegalArgumentException("scope must be analytics or algorithm");
        }
        byte[] content = analyticsService.export(new AnalyticsDtos.AnalyticsFilter(from, to, algorithmType,
                userType, userLevel, departmentId), scope);
        String filename = "algorithm".equalsIgnoreCase(scope) ? "algorithm-results.csv" : "analytics-report.csv";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv;charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return ResponseEntity.ok().headers(headers).body(content);
    }
}

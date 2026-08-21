package com.library.cost.controller;

import com.library.cost.common.ApiResponse;
import com.library.cost.dto.AnalysisQuery;
import com.library.cost.dto.AnalysisResponse;
import com.library.cost.dto.CostAnalysisItem;
import com.library.cost.dto.CostSummaryDTO;
import com.library.cost.exporter.CostExporter;
import com.library.cost.service.CostService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cost")
public class CostController {

    private final CostService costService;
    private final CostExporter costExporter;

    public CostController(CostService costService, CostExporter costExporter) {
        this.costService = costService;
        this.costExporter = costExporter;
    }

    @GetMapping("/summary")
    public ApiResponse<CostSummaryDTO> summary(@RequestParam(defaultValue = "") String year) {
        return ApiResponse.ok(costService.summary(year));
    }

    @GetMapping("/analysis")
    public ApiResponse<AnalysisResponse> analysis(AnalysisQuery query) {
        return ApiResponse.ok(costService.analysis(query));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(AnalysisQuery query,
                                         @RequestParam(defaultValue = "xlsx") String format) throws IOException {
        List<CostAnalysisItem> items = costService.queryItems(query);
        String ext = "csv".equalsIgnoreCase(format) ? "csv" : "xlsx";
        String year = (query.getYear() == null || query.getYear().isBlank()) ? "all" : query.getYear();

        byte[] body;
        String contentType;
        if ("csv".equalsIgnoreCase(format)) {
            body = costExporter.toCsv(items);
            contentType = "text/csv;charset=UTF-8";
        } else {
            body = costExporter.toXlsx(items);
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cost-report-" + year + "." + ext + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(body);
    }
}
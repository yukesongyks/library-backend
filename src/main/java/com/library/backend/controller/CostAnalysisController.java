package com.library.backend.controller;

import com.library.backend.dto.CostQueryRequest;
import com.library.backend.dto.CostSummaryDTO;
import com.library.backend.dto.DimensionStatDTO;
import com.library.backend.dto.ProjectCostDTO;
import com.library.backend.service.CostAnalysisService;
import com.library.backend.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
public class CostAnalysisController {

    private final CostAnalysisService costAnalysisService;
    private final ExcelExportService excelExportService;

    /**
     * W01 成本汇总查询
     */
    @GetMapping("/summary")
    public CostSummaryDTO getSummary(@Valid CostQueryRequest request) {
        return costAnalysisService.getCostSummary(request);
    }

    /**
     * W02 月度趋势查询
     */
    @GetMapping("/trend")
    public List<DimensionStatDTO> getMonthlyTrend(@RequestParam(required = false) Integer year) {
        return costAnalysisService.getMonthlyTrend(year);
    }

    /**
     * W03 人力成本角色占比查询
     */
    @GetMapping("/role")
    public List<DimensionStatDTO> getCostByRole() {
        return costAnalysisService.getCostByRole();
    }

    /**
     * W04 项目成本查询
     */
    @GetMapping("/project")
    public List<ProjectCostDTO> getProjectCost(@RequestParam(required = false) Integer budgetYear) {
        return costAnalysisService.getProjectCost(budgetYear);
    }

    /**
     * W05 维度聚合查询
     */
    @GetMapping("/dimension")
    public List<DimensionStatDTO> getCostByDimension(
            @RequestParam String dimension,
            @RequestParam(required = false) Integer year) {
        return costAnalysisService.getCostByDimension(dimension, year);
    }

    /**
     * W06 成本汇总 Excel 导出
     */
    @GetMapping("/export/summary")
    public ResponseEntity<byte[]> exportSummary(@Valid CostQueryRequest request) throws IOException {
        CostSummaryDTO summary = costAnalysisService.getCostSummary(request);
        byte[] bytes = excelExportService.exportCostSummary(summary);
        String filename = URLEncoder.encode("成本汇总报表.xlsx", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(bytes);
    }

    /**
     * W07 项目成本 Excel 导出
     */
    @GetMapping("/export/project")
    public ResponseEntity<byte[]> exportProjectCost(
            @RequestParam(required = false) Integer budgetYear) throws IOException {
        List<ProjectCostDTO> projects = costAnalysisService.getProjectCost(budgetYear);
        byte[] bytes = excelExportService.exportProjectCost(projects);
        String filename = URLEncoder.encode("项目成本报表.xlsx", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(bytes);
    }
}

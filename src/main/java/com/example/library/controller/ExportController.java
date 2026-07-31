package com.example.library.controller;

import com.example.library.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 导出接口。
 * GET /api/export?tab=&format=，csv 返回文件流，json 返回 JSON。
 * 对应 tasks E1 / spec Feature: 导出。
 */
@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<?> export(
            @RequestParam String tab,
            @RequestParam String format) {
        exportService.validateTab(tab);
        exportService.validateFormat(format);
        List<Map<String, Object>> data = exportService.exportData(tab);

        if ("csv".equals(format)) {
            String csv = exportService.toCsv(data);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + tab + ".csv");
            return ResponseEntity.ok().headers(headers).body(csv);
        } else {
            return ResponseEntity.ok().body(data);
        }
    }
}

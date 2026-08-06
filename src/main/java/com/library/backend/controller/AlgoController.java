package com.library.backend.controller;

import com.library.backend.dto.*;
import com.library.backend.service.BubbleSortService;
import com.library.backend.service.ExportService;
import com.library.backend.service.HashService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/algo")
@RequiredArgsConstructor
public class AlgoController {

    private final HashService hashService;
    private final BubbleSortService bubbleSortService;
    private final ExportService exportService;

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello, World!");
    }

    @PostMapping("/hash")
    public HashResponse hash(@Valid @RequestBody HashRequest request) {
        String digest = hashService.sha256Hex(request.getInput());
        return new HashResponse("SHA-256", digest);
    }

    @PostMapping("/bubble-sort")
    public BubbleSortResponse bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        return bubbleSortService.sort(request.getNumbers());
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@Valid @RequestBody ExportRequest request) throws IOException {
        String type = request.getType();
        String format = request.getFormat();

        if (!Set.of("hello", "hash", "bubble-sort").contains(type)) {
            throw new IllegalArgumentException("不支持的导出类型: " + type);
        }
        if (!Set.of("csv", "xlsx").contains(format)) {
            throw new IllegalArgumentException("不支持的导出格式: " + format);
        }

        byte[] content;
        String contentType;
        String fileName;

        if ("csv".equals(format)) {
            content = exportService.exportCsv(type, request.getData());
            contentType = "text/csv";
            fileName = type + "-result.csv";
        } else {
            content = exportService.exportXlsx(type, request.getData());
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            fileName = type + "-result.xlsx";
        }

        // Check 10MB limit
        if (content.length > 10 * 1024 * 1024) {
            throw new PayloadTooLargeException("导出文件大小超过 10MB 上限");
        }

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.parseMediaType(contentType))
                .body(content);
    }

    /**
     * Custom exception for payload too large scenarios.
     */
    public static class PayloadTooLargeException extends RuntimeException {
        public PayloadTooLargeException(String message) {
            super(message);
        }
    }
}

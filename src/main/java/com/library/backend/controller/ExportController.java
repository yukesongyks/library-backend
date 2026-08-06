package com.library.backend.controller;

import com.library.backend.dto.ExportRequest;
import com.library.backend.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/export")
    public void export(@RequestBody ExportRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> filters = request.getFilters();
        String startDate = filters != null ? filters.get("startDate") : null;
        String endDate = filters != null ? filters.get("endDate") : null;
        String apiName = filters != null ? filters.get("apiName") : null;

        exportService.export(
                request.getTab(),
                startDate,
                endDate,
                apiName,
                response
        );
    }
}

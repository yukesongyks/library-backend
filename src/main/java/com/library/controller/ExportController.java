package com.library.controller;

import com.library.dto.ExportRequest;
import com.library.service.ExportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@Valid @RequestBody ExportRequest request) {
        request.validate();
        byte[] fileBytes = exportService.export(request.getType(), request.getData(), request.getFormat());
        String filename = request.getType() + "_result." + request.getFormat();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }
}
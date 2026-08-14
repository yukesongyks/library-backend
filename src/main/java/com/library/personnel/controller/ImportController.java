package com.library.personnel.controller;

import com.library.personnel.dto.response.ApiResponse;
import com.library.personnel.dto.response.ImportResultResponse;
import com.library.personnel.service.ImportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<ImportResultResponse>> importEmployees(
            @RequestParam("file") MultipartFile file) {
        ImportResultResponse result = importService.importEmployees(file);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] template = importService.generateTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "employee_import_template.xlsx");
        return ResponseEntity.ok().headers(headers).body(template);
    }
}
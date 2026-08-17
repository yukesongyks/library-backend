package com.example.library.controller;

import com.example.library.common.ApiResult;
import com.example.library.dto.BubbleSortRequest;
import com.example.library.dto.BubbleSortResponse;
import com.example.library.dto.ExportRequest;
import com.example.library.dto.HashRequest;
import com.example.library.dto.HashResponse;
import com.example.library.dto.HelloWorldResponse;
import com.example.library.service.BubbleSortService;
import com.example.library.service.ExportResult;
import com.example.library.service.ExportService;
import com.example.library.service.HashService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/api")
public class AlgorithmController {

    private final HashService hashService;
    private final BubbleSortService bubbleSortService;
    private final ExportService exportService;

    public AlgorithmController(HashService hashService, BubbleSortService bubbleSortService, ExportService exportService) {
        this.hashService = hashService;
        this.bubbleSortService = bubbleSortService;
        this.exportService = exportService;
    }

    @GetMapping("/helloworld")
    public ApiResult<HelloWorldResponse> helloWorld() {
        HelloWorldResponse response = new HelloWorldResponse("Hello, World!", Instant.now().toString());
        return ApiResult.ok(response);
    }

    @PostMapping("/hash")
    public ApiResult<HashResponse> hash(@Valid @RequestBody HashRequest request) {
        HashResponse response = hashService.computeHash(request.getInput(), request.getAlgorithm());
        return ApiResult.ok(response);
    }

    @PostMapping("/bubblesort")
    public ApiResult<BubbleSortResponse> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        BubbleSortResponse response = bubbleSortService.sort(request.getArray());
        return ApiResult.ok(response);
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@Valid @RequestBody ExportRequest request) {
        ExportResult result = exportService.export(request.getType(), request.getData(), request.getFormat());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.getFilename() + "\"")
                .body(result.getContent());
    }
}
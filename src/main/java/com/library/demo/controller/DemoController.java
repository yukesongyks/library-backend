package com.library.demo.controller;

import com.library.demo.annotation.CallLog;
import com.library.demo.dto.request.BubbleSortRequest;
import com.library.demo.dto.request.ExportRequest;
import com.library.demo.dto.request.HashRequest;
import com.library.demo.dto.request.HelloWorldRequest;
import com.library.demo.dto.response.*;
import com.library.demo.enums.ApiType;
import com.library.demo.service.BubbleSortService;
import com.library.demo.service.ExportService;
import com.library.demo.service.HashService;
import com.library.demo.service.HelloWorldService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class DemoController {

    private final HelloWorldService helloWorldService;
    private final HashService hashService;
    private final BubbleSortService bubbleSortService;
    private final ExportService exportService;

    @CallLog(apiType = ApiType.HELLOWORLD)
    @PostMapping("/helloworld")
    public DemoResponse<HelloWorldResult> helloWorld(@RequestBody HelloWorldRequest request) {
        return DemoResponse.success(helloWorldService.greet(request));
    }

    @CallLog(apiType = ApiType.HASH)
    @PostMapping("/hash")
    public DemoResponse<HashResult> hash(@Valid @RequestBody HashRequest request) {
        return DemoResponse.success(hashService.hash(request));
    }

    @CallLog(apiType = ApiType.BUBBLE_SORT)
    @PostMapping("/bubble-sort")
    public DemoResponse<BubbleSortResult> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        return DemoResponse.success(bubbleSortService.sort(request));
    }

    @PostMapping("/export")
    public void export(@Valid @RequestBody ExportRequest request,
                       HttpServletResponse response) throws IOException {
        byte[] data = exportService.export(request);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + request.getType() + "_export.xlsx");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}

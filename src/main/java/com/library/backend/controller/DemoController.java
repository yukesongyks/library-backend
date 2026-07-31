package com.library.backend.controller;

import com.library.backend.common.Result;
import com.library.backend.model.dto.BubbleSortRequest;
import com.library.backend.model.dto.BubbleSortResponse;
import com.library.backend.model.dto.HashRequest;
import com.library.backend.model.dto.HashResponse;
import com.library.backend.model.dto.HelloWorldResponse;
import com.library.backend.service.DemoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo business endpoints (design spec §4.2–§4.4), all under {@code /api/demo}.
 *
 * <p>Per-interface validation (§5.2.2) is intentionally implemented as business
 * prechecks in {@link DemoService} — empty/too-long inputs throw
 * {@code IllegalArgumentException}, which {@code GlobalExceptionHandler} maps to
 * code <b>40002</b> (business rule). This deliberately avoids bean-validation
 * annotations on the request fields, because {@code @NotBlank}/{@code @Size}
 * failures would surface as {@code MethodArgumentNotValidException} → 40001,
 * contradicting the spec's 40002 mapping for these specific rules. The
 * {@code MethodArgumentNotValidException} handler is still present in
 * {@code GlobalExceptionHandler} for completeness/§5.2.1.
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    /** §4.2 GET /api/demo/helloworld — no input. */
    @GetMapping("/helloworld")
    public Result<HelloWorldResponse> helloWorld() {
        return Result.success(demoService.helloWorld());
    }

    /** §4.3 POST /api/demo/hash — body {"text":"..."}. */
    @PostMapping("/hash")
    public Result<HashResponse> hash(@RequestBody HashRequest request) {
        String text = request == null ? null : request.text();
        return Result.success(demoService.hash(text));
    }

    /** §4.4 POST /api/demo/bubble-sort — body {"numbers":[...]}. */
    @PostMapping("/bubble-sort")
    public Result<BubbleSortResponse> bubbleSort(@RequestBody BubbleSortRequest request) {
        var numbers = request == null ? null : request.numbers();
        return Result.success(demoService.bubbleSort(numbers));
    }
}

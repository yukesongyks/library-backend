package com.library.controller;

import com.library.common.Result;
import com.library.dto.BubbleSortRequest;
import com.library.service.BubbleSortService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/bubblesort")
    public Result<Map<String, Object>> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        request.validate();
        BubbleSortService.BubbleSortResult result =
                bubbleSortService.sort(request.getArray(), request.getOrder());
        Map<String, Object> data = new HashMap<>();
        data.put("sorted", result.sorted());
        data.put("steps", result.steps());
        data.put("original", request.getArray());
        return Result.ok(data);
    }
}
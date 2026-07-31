package com.example.library.controller;

import com.example.library.aspect.TrackCall;
import com.example.library.dto.BubbleSortRequest;
import com.example.library.service.AlgorithmService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 冒泡排序接口。
 * POST /api/bubble-sort，返回 input/sorted/steps。
 * 对应 tasks C3 / spec Scenario: 正常排序 / 空数组。
 */
@RestController
@RequestMapping("/api")
public class BubbleSortController {

    private final AlgorithmService algorithmService;

    public BubbleSortController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @TrackCall("bubble-sort")
    @PostMapping("/bubble-sort")
    public Map<String, Object> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        AlgorithmService.BubbleSortResult result = algorithmService.bubbleSort(request.getNumbers());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("input", result.getInput());
        response.put("sorted", result.getSorted());
        response.put("steps", result.getSteps());
        return response;
    }
}

package com.library.backend.controller;

import com.library.backend.dto.ApiRequest;
import com.library.backend.dto.ApiResponse;
import com.library.backend.service.BubbleSortService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BubbleSortController {
    
    private final BubbleSortService bubbleSortService;
    
    @PostMapping("/bubble-sort")
    public ApiResponse<Map<String, Object>> bubbleSort(@RequestBody ApiRequest request) {
        int[] array = request.getArray();
        if (array == null) {
            return ApiResponse.error(400, "Array is required");
        }
        
        int[] sorted = bubbleSortService.sort(array);
        
        Map<String, Object> result = new HashMap<>();
        result.put("original", Arrays.stream(array).boxed().toList());
        result.put("sorted", Arrays.stream(sorted).boxed().toList());
        
        return ApiResponse.success(result);
    }
}

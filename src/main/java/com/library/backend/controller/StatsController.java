package com.library.backend.controller;

import com.library.backend.dto.ApiResponse;
import com.library.backend.dto.StatsQuery;
import com.library.backend.dto.StatsResponse;
import com.library.backend.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatsController {
    
    private final StatsService statsService;
    
    @GetMapping("/stats")
    public ApiResponse<StatsResponse> getStats(
            @RequestParam String dimension,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String apiName) {
        
        StatsResponse response = statsService.getStats(dimension, startDate, endDate, apiName);
        return ApiResponse.success(response);
    }
}

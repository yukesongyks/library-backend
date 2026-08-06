package com.library.backend.controller;

import com.library.backend.dto.ApiRequest;
import com.library.backend.dto.ApiResponse;
import com.library.backend.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HashController {
    
    private final HashService hashService;
    
    @PostMapping("/hash")
    public ApiResponse<Map<String, String>> hash(@RequestBody ApiRequest request) {
        String input = request.getInput();
        if (input == null || input.isEmpty()) {
            return ApiResponse.error(400, "Input is required");
        }
        String hash = hashService.hash(input);
        return ApiResponse.success(Map.of("input", input, "hash", hash));
    }
}

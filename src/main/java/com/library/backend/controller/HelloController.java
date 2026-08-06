package com.library.backend.controller;

import com.library.backend.dto.ApiRequest;
import com.library.backend.dto.ApiResponse;
import com.library.backend.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HelloController {
    
    private final HelloService helloService;
    
    @PostMapping("/hello")
    public ApiResponse<Map<String, String>> hello(@RequestBody ApiRequest request) {
        String message = helloService.sayHello();
        return ApiResponse.success(Map.of("message", message));
    }
}

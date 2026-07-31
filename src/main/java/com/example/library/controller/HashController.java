package com.example.library.controller;

import com.example.library.aspect.TrackCall;
import com.example.library.dto.HashRequest;
import com.example.library.service.AlgorithmService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 哈希算法接口。
 * POST /api/hash，支持 SHA-256/SHA-512/MD5，非法算法返回 400。
 * 对应 tasks C2 / spec Scenario: SHA-256 哈希 / 不支持的算法。
 */
@RestController
@RequestMapping("/api")
public class HashController {

    private final AlgorithmService algorithmService;

    public HashController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @TrackCall("hash")
    @PostMapping("/hash")
    public Map<String, String> hash(@Valid @RequestBody HashRequest request) {
        String hash = algorithmService.hash(request.getAlgorithm(), request.getInput());
        Map<String, String> result = new LinkedHashMap<>();
        result.put("algorithm", request.getAlgorithm());
        result.put("input", request.getInput());
        result.put("hash", hash);
        return result;
    }
}

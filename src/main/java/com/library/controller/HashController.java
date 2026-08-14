package com.library.controller;

import com.library.common.Result;
import com.library.dto.HashRequest;
import com.library.service.HashService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public Result<Map<String, Object>> hash(@Valid @RequestBody HashRequest request) {
        request.validate();
        String hash = hashService.compute(request.getInput(), request.getAlgorithm());
        Map<String, Object> data = new HashMap<>();
        data.put("hash", hash);
        data.put("algorithm", request.getAlgorithm());
        data.put("input", request.getInput());
        return Result.ok(data);
    }
}
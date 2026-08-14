package com.library.controller;

import com.library.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/helloworld")
    public Result<Map<String, Object>> helloworld(
            @RequestParam(defaultValue = "World") String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello, " + name + "!");
        data.put("timestamp", System.currentTimeMillis());
        return Result.ok(data);
    }
}
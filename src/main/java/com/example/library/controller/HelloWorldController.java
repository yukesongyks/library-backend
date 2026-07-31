package com.example.library.controller;

import com.example.library.aspect.TrackCall;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * HelloWorld 接口。
 * GET /api/hello-world → { "result": "Hello, World!" }
 * 对应 tasks C1 / spec Scenario: 正常调用 helloworld。
 */
@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @TrackCall("helloworld")
    @GetMapping("/hello-world")
    public Map<String, String> helloWorld(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return Map.of("result", "Hello, World!");
    }
}

package com.library.backend.demo.controller;

import com.library.backend.common.Result;
import com.library.backend.demo.dto.BubbleSortRequest;
import com.library.backend.demo.dto.BubbleSortResponse;
import com.library.backend.demo.dto.HashRequest;
import com.library.backend.demo.dto.HashResponse;
import com.library.backend.demo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Demo 接口控制器
 */
@RestController
@RequestMapping("/openapi/demo")
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    /**
     * API-01: HelloWorld
     */
    @GetMapping("/helloworld")
    public Result<String> helloWorld() {
        log.info("收到 HelloWorld 请求");
        String result = demoService.helloWorld();
        return Result.success(result);
    }

    /**
     * API-02: 哈希算法
     */
    @PostMapping("/hash")
    public Result<HashResponse> hash(@Valid @RequestBody HashRequest request) {
        log.info("收到哈希计算请求: input={}, algorithm={}", request.getInput(), request.getAlgorithm());
        HashResponse response = demoService.hash(request.getInput(), request.getAlgorithm());
        return Result.success(response);
    }

    /**
     * API-03: 冒泡排序
     */
    @PostMapping("/bubble-sort")
    public Result<BubbleSortResponse> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        log.info("收到冒泡排序请求: size={}", request.getNumbers() != null ? request.getNumbers().size() : 0);
        BubbleSortResponse response = demoService.bubbleSort(request.getNumbers());
        return Result.success(response);
    }
}

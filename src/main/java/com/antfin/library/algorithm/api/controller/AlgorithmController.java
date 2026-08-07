package com.antfin.library.algorithm.api.controller;

import com.antfin.library.algorithm.model.request.BubbleSortRequest;
import com.antfin.library.algorithm.model.request.HashRequest;
import com.antfin.library.algorithm.model.vo.BubbleSortResultVO;
import com.antfin.library.algorithm.model.vo.HashResultVO;
import com.antfin.library.algorithm.model.vo.HelloWorldVO;
import com.antfin.library.algorithm.service.AlgorithmService;
import com.antfin.library.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 算法接口
 */
@RestController
@RequestMapping("/api/algorithm")
public class AlgorithmController {

    @Autowired
    private AlgorithmService algorithmService;

    /**
     * W01 HelloWorld
     */
    @GetMapping("/hello-world")
    public Result<HelloWorldVO> helloWorld() {
        return Result.success(algorithmService.helloWorld());
    }

    /**
     * W02 哈希算法
     */
    @PostMapping("/hash")
    public Result<HashResultVO> hash(@Valid @RequestBody HashRequest request) {
        return Result.success(algorithmService.hash(request.getInputText(), request.getAlgorithm()));
    }

    /**
     * W03 冒泡排序
     */
    @PostMapping("/bubble-sort")
    public Result<BubbleSortResultVO> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        return Result.success(algorithmService.bubbleSort(request.getNumbers()));
    }
}

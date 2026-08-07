package com.antdigital.library.demo.controller;

import com.antdigital.library.common.response.ApiResponse;
import com.antdigital.library.demo.model.vo.BubbleSortVO;
import com.antdigital.library.demo.model.vo.HashVO;
import com.antdigital.library.demo.model.vo.HelloWorldVO;
import com.antdigital.library.demo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示功能 Controller。
 *
 * <p>提供 HelloWorld、哈希算法、冒泡排序三个接口。</p>
 *
 * @author library-backend
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    /**
     * HelloWorld 接口。
     *
     * @return 欢迎消息
     */
    @GetMapping("/helloworld")
    public ApiResponse<HelloWorldVO> helloWorld() {
        logger.info("收到 helloworld 请求");
        return ApiResponse.success(demoService.helloWorld());
    }

    /**
     * 哈希算法接口。
     *
     * @param input 原始字符串
     * @return 哈希结果
     */
    @GetMapping("/hash")
    public ApiResponse<HashVO> hash(@RequestParam String input) {
        logger.info("收到 hash 请求, input: {}", input);
        return ApiResponse.success(demoService.hash(input));
    }

    /**
     * 冒泡排序接口。
     *
     * @param input 逗号分隔的数字串
     * @return 排序结果
     */
    @GetMapping("/bubble-sort")
    public ApiResponse<BubbleSortVO> bubbleSort(@RequestParam String input) {
        logger.info("收到 bubble-sort 请求, input: {}", input);
        return ApiResponse.success(demoService.bubbleSort(input));
    }
}

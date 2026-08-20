package com.library.demo.controller;

import com.library.demo.model.request.BubbleSortRequest;
import com.library.demo.model.request.HashRequest;
import com.library.demo.model.request.HelloWorldRequest;
import com.library.demo.model.response.ApiResponse;
import com.library.demo.model.response.BubbleSortResponse;
import com.library.demo.model.response.HashResponse;
import com.library.demo.model.response.HelloWorldResponse;
import com.library.demo.service.BubbleSortService;
import com.library.demo.service.HashService;
import com.library.demo.service.HelloWorldService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final HelloWorldService helloWorldService;
    private final HashService hashService;
    private final BubbleSortService bubbleSortService;

    public DemoController(HelloWorldService helloWorldService,
                          HashService hashService,
                          BubbleSortService bubbleSortService) {
        this.helloWorldService = helloWorldService;
        this.hashService = hashService;
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/helloworld")
    public ApiResponse<HelloWorldResponse> helloWorld(@RequestBody(required = false) HelloWorldRequest request) {
        String name = (request != null) ? request.getName() : null;
        return ApiResponse.success(helloWorldService.greet(name));
    }

    @PostMapping("/hash")
    public ApiResponse<HashResponse> hash(@Valid @RequestBody HashRequest request) {
        return ApiResponse.success(hashService.hash(request.getInput(), request.getAlgorithm()));
    }

    @PostMapping("/bubble-sort")
    public ApiResponse<BubbleSortResponse> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        return ApiResponse.success(bubbleSortService.sort(request.getNumbers(), request.getOrder()));
    }
}

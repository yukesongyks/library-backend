package com.example.library.api;

import com.example.library.service.AlgorithmService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {
    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @PostMapping("/hello-world")
    public AlgorithmDtos.HelloWorldResponse helloWorld(@Valid @RequestBody AlgorithmDtos.HelloWorldRequest request,
                                                       HttpServletRequest httpRequest) {
        return algorithmService.helloWorld(request, httpRequest);
    }

    @PostMapping("/hash")
    public AlgorithmDtos.HashResponse hash(@Valid @RequestBody AlgorithmDtos.HashRequest request,
                                           HttpServletRequest httpRequest) {
        return algorithmService.hash(request, httpRequest);
    }

    @PostMapping("/bubble-sort")
    public AlgorithmDtos.BubbleSortResponse bubbleSort(@Valid @RequestBody AlgorithmDtos.BubbleSortRequest request,
                                                       HttpServletRequest httpRequest) {
        return algorithmService.bubbleSort(request, httpRequest);
    }
}

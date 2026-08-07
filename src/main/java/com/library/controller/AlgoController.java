package com.library.controller;

import com.library.dto.AlgoResult;
import com.library.service.AlgoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/algo")
public class AlgoController {
    private final AlgoService algoService;

    public AlgoController(AlgoService algoService) {
        this.algoService = algoService;
    }

    @GetMapping("/helloworld")
    public AlgoResult helloworld() {
        return algoService.helloworld();
    }

    @GetMapping("/hash")
    public AlgoResult hash(@RequestParam(defaultValue = "hello") String input) {
        return algoService.hash(input);
    }

    @GetMapping("/bubblesort")
    public AlgoResult bubblesort(@RequestParam(defaultValue = "5,3,8,1,9,2,7") String input) {
        return algoService.bubblesort(input);
    }
}

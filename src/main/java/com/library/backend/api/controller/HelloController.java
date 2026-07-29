package com.library.backend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World 接口
 *
 * @author library
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    /**
     * hello world 接口
     *
     * @return hello world
     */
    @GetMapping
    public String hello() {
        return "hello world";
    }
}

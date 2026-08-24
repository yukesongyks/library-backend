package com.company.library.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World 示例控制器。
 */
@RestController
public class HelloController {

    /**
     * 返回 Hello World 字符串。
     *
     * @return "Hello World"
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
package com.library.demo.service;

import com.library.demo.model.response.HelloWorldResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HelloWorldService {

    public HelloWorldResponse greet(String name) {
        String target = (name == null || name.isBlank()) ? "World" : name;
        return new HelloWorldResponse("Hello, " + target + "!", LocalDateTime.now());
    }
}

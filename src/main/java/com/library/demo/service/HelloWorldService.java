package com.library.demo.service;

import com.library.demo.dto.request.HelloWorldRequest;
import com.library.demo.dto.response.HelloWorldResult;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HelloWorldService {

    public HelloWorldResult greet(HelloWorldRequest request) {
        long start = System.currentTimeMillis();
        String name = (request.getName() != null && !request.getName().isBlank())
                ? request.getName() : "World";
        String result = "Hello, " + name + "!";
        long elapsed = System.currentTimeMillis() - start;
        return new HelloWorldResult(result, Instant.now().toString(), elapsed);
    }
}

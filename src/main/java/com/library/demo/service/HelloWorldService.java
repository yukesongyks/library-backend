package com.library.demo.service;

import com.library.demo.model.response.HelloWorldResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class HelloWorldService {

    private static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");

    public HelloWorldResponse greet(String name) {
        String target = (name == null || name.isBlank()) ? "World" : name;
        return new HelloWorldResponse("Hello, " + target + "!", LocalDateTime.now(ZONE_SHANGHAI));
    }
}

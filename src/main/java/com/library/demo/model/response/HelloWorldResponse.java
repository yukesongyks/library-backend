package com.library.demo.model.response;

import java.time.LocalDateTime;

public class HelloWorldResponse {
    private String result;
    private LocalDateTime timestamp;

    public HelloWorldResponse(String result, LocalDateTime timestamp) {
        this.result = result;
        this.timestamp = timestamp;
    }

    public String getResult() { return result; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

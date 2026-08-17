package com.example.library.dto;

public class HelloWorldResponse {

    private String greeting;
    private String timestamp;

    public HelloWorldResponse() {
    }

    public HelloWorldResponse(String greeting, String timestamp) {
        this.greeting = greeting;
        this.timestamp = timestamp;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
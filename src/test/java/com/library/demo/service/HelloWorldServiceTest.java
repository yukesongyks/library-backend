package com.library.demo.service;

import com.library.demo.model.response.HelloWorldResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldServiceTest {

    private final HelloWorldService service = new HelloWorldService();

    @Test
    void greet_withName_returnsPersonalizedGreeting() {
        HelloWorldResponse response = service.greet("Alice");
        assertEquals("Hello, Alice!", response.getResult());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void greet_withNull_returnsDefaultGreeting() {
        HelloWorldResponse response = service.greet(null);
        assertEquals("Hello, World!", response.getResult());
    }

    @Test
    void greet_withBlank_returnsDefaultGreeting() {
        HelloWorldResponse response = service.greet("  ");
        assertEquals("Hello, World!", response.getResult());
    }
}

package com.library.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Library backend application entry point.
 *
 * <p>Implements the HelloWorld demo contract (§4): helloworld, hash (SHA-256),
 * bubble sort, and CSV export endpoints under {@code /api/demo/*}.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

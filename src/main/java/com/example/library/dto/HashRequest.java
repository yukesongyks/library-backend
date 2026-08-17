package com.example.library.dto;

import javax.validation.constraints.NotBlank;

/**
 * Request DTO for hash computation API.
 */
public class HashRequest {

    @NotBlank(message = "Input must not be empty")
    private String input;

    private String algorithm = "SHA-256";

    public HashRequest() {
    }

    public HashRequest(String input, String algorithm) {
        this.input = input;
        this.algorithm = algorithm;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
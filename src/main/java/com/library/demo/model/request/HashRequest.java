package com.library.demo.model.request;

import jakarta.validation.constraints.NotBlank;

public class HashRequest {
    @NotBlank(message = "input is required")
    private String input;
    private String algorithm; // MD5 | SHA-1 | SHA-256, default SHA-256

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}

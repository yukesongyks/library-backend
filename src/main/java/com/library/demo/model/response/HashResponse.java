package com.library.demo.model.response;

import java.time.LocalDateTime;

public class HashResponse {
    private String input;
    private String algorithm;
    private String hashValue;
    private LocalDateTime timestamp;

    public HashResponse(String input, String algorithm, String hashValue, LocalDateTime timestamp) {
        this.input = input;
        this.algorithm = algorithm;
        this.hashValue = hashValue;
        this.timestamp = timestamp;
    }

    public String getInput() { return input; }
    public String getAlgorithm() { return algorithm; }
    public String getHashValue() { return hashValue; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

package com.library.backend.demo.dto;

/**
 * 哈希计算响应
 */
public class HashResponse {

    private String input;
    private String algorithm;
    private String hashValue;

    public HashResponse() {
    }

    public HashResponse(String input, String algorithm, String hashValue) {
        this.input = input;
        this.algorithm = algorithm;
        this.hashValue = hashValue;
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

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
}

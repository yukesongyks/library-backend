package com.example.library.dto;

import javax.validation.constraints.NotBlank;

/**
 * 哈希请求 DTO。对应 spec POST /api/hash。
 */
public class HashRequest {

    @NotBlank(message = "algorithm 不能为空")
    private String algorithm;

    @NotBlank(message = "input 不能为空")
    private String input;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}

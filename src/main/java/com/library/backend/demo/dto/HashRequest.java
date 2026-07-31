package com.library.backend.demo.dto;

import javax.validation.constraints.NotBlank;

/**
 * 哈希计算请求
 */
public class HashRequest {

    @NotBlank(message = "input 不能为空")
    private String input;

    /** 哈希算法：SHA256（默认）/ MD5 */
    private String algorithm;

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

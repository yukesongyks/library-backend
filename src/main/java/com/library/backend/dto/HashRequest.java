package com.library.backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.List;

/**
 * 哈希算法请求体。
 *
 * <p>spec {@code algorithms.md}：
 * <ul>
 *   <li>{@code text} 为空 → HTTP 400 {@code {"error":"text must not be empty"}}</li>
 *   <li>{@code algorithm} 不支持 → HTTP 400 {@code {"error":"Unsupported algorithm: <name>"}}</li>
 * </ul>
 * 使用 {@code @NotBlank} 保证空文本触发校验失败，由全局异常处理器转 400。
 */
public class HashRequest {

    @NotBlank(message = "text must not be empty")
    private String text;

    @NotBlank(message = "algorithm must not be empty")
    private String algorithm;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}

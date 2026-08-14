package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class HashRequest {
    @NotBlank(message = "input不能为空")
    private String input;
    private String algorithm = "SHA-256";

    private static final Set<String> SUPPORTED = Set.of("MD5", "SHA-1", "SHA-256", "SHA-512");

    public void validate() {
        if (!SUPPORTED.contains(algorithm)) {
            throw new IllegalArgumentException("不支持的算法: " + algorithm
                + "，可选: " + String.join(", ", SUPPORTED));
        }
    }
}
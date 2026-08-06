package com.library.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HashRequest {
    @NotBlank(message = "input 不能为空")
    private String input;
}

package com.library.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HashRequest {
    @NotBlank(message = "input is required")
    private String input;
    private String algorithm; // MD5|SHA1|SHA256|SHA512, default SHA256
}

package com.library.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashResult {
    private String input;
    private String algorithm;
    private String hashResult;
    private String timestamp;
    private long executionTimeMs;
}

package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HashResponse {
    private String algorithm;
    private String digest;
}

package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DimensionStatDTO {
    private String dimensionName;
    private BigDecimal amount;
    private Double percentage;
}

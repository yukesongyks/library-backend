package com.library.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CostSummaryDTO {
    private BigDecimal totalCost;
    private BigDecimal laborCost;
    private Integer recordCount;
    private List<DimensionStatDTO> byDepartment;
    private List<DimensionStatDTO> byRole;
    private List<DimensionStatDTO> byMonth;
}

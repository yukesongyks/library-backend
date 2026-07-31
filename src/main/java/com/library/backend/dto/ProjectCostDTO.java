package com.library.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class ProjectCostDTO {
    private Long projectId;
    private String projectName;
    private BigDecimal budgetAmount;
    private BigDecimal actualCost;
    private BigDecimal budgetUsageRate;
    private BigDecimal overspendAmount;

    public void calculateDerived() {
        BigDecimal actual = actualCost != null ? actualCost : BigDecimal.ZERO;
        if (budgetAmount != null && budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.budgetUsageRate = actual
                .multiply(new BigDecimal("100"))
                .divide(budgetAmount, 2, RoundingMode.HALF_UP);
            this.overspendAmount = actual.subtract(budgetAmount).max(BigDecimal.ZERO);
        } else {
            this.budgetUsageRate = BigDecimal.ZERO;
            this.overspendAmount = BigDecimal.ZERO;
        }
    }
}

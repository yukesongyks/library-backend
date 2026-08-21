package com.library.cost.dto;

import java.math.BigDecimal;

public record CostAnalysisItem(String name, BigDecimal laborCost,
                               BigDecimal projectBudget, BigDecimal projectActual,
                               double budgetRatio, BigDecimal overBudgetAmount) {
}

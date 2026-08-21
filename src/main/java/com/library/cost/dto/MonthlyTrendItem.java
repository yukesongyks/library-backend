package com.library.cost.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyTrendItem(String month, BigDecimal laborCost,
                               BigDecimal projectCost, BigDecimal totalCost) {
}

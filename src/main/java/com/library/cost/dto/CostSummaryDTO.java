package com.library.cost.dto;

import java.math.BigDecimal;
import java.util.List;

public record CostSummaryDTO(BigDecimal totalCost, BigDecimal laborCost,
                             BigDecimal projectCost, double laborRatio,
                             double projectRatio, int overBudgetCount,
                             List<MonthlyTrendItem> monthlyTrend) {
}

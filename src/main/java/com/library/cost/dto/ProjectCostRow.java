package com.library.cost.dto;

import java.math.BigDecimal;

public record ProjectCostRow(Long projectId, String month, BigDecimal actualAmount,
                             Long departmentId, Long businessLineId,
                             BigDecimal budgetAmount) {
}

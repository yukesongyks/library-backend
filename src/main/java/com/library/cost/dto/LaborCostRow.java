package com.library.cost.dto;

import java.math.BigDecimal;

public record LaborCostRow(Long projectId, String month, BigDecimal amount,
                           String role, Long departmentId, Long businessLineId,
                           Long employeeId) {
}

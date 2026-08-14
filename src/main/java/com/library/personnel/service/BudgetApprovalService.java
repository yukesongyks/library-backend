package com.library.personnel.service;

import com.library.personnel.enums.WhitelistType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BudgetApprovalService {

    private final WhitelistService whitelistService;

    @Value("${personnel.budget.approval-threshold:10000}")
    private BigDecimal approvalThreshold;

    public BudgetApprovalService(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    /**
     * Check if a budget change requires approval.
     *
     * @param changeAmount the amount of change
     * @param employeeId the employee requesting the change
     * @return true if approval is required
     */
    public boolean requiresApproval(BigDecimal changeAmount, String employeeId) {
        if (changeAmount.compareTo(approvalThreshold) > 0) {
            return !whitelistService.checkWhitelist(employeeId, WhitelistType.APPROVAL);
        }
        return false;
    }

    /**
     * Check if an employee is authorized to approve budget changes.
     */
    public boolean canApprove(String employeeId) {
        return whitelistService.checkWhitelist(employeeId, WhitelistType.APPROVAL);
    }

    public BigDecimal getApprovalThreshold() {
        return approvalThreshold;
    }
}
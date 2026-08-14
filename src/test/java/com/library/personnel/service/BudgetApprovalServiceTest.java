package com.library.personnel.service;

import com.library.personnel.enums.WhitelistType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetApprovalServiceTest {

    @Mock
    private WhitelistService whitelistService;

    private BudgetApprovalService budgetApprovalService;

    @BeforeEach
    void setUp() {
        budgetApprovalService = new BudgetApprovalService(whitelistService);
        ReflectionTestUtils.setField(budgetApprovalService, "approvalThreshold", new BigDecimal("10000"));
    }

    @Test
    void testRequiresApprovalBelowThreshold() {
        boolean result = budgetApprovalService.requiresApproval(new BigDecimal("5000"), "EMP000001");
        assertFalse(result);
        verify(whitelistService, never()).checkWhitelist(anyString(), any());
    }

    @Test
    void testRequiresApprovalAboveThresholdNotInWhitelist() {
        when(whitelistService.checkWhitelist("EMP000001", WhitelistType.APPROVAL)).thenReturn(false);

        boolean result = budgetApprovalService.requiresApproval(new BigDecimal("15000"), "EMP000001");

        assertTrue(result);
        verify(whitelistService).checkWhitelist("EMP000001", WhitelistType.APPROVAL);
    }

    @Test
    void testRequiresApprovalAboveThresholdInWhitelist() {
        when(whitelistService.checkWhitelist("EMP000001", WhitelistType.APPROVAL)).thenReturn(true);

        boolean result = budgetApprovalService.requiresApproval(new BigDecimal("15000"), "EMP000001");

        assertFalse(result);
        verify(whitelistService).checkWhitelist("EMP000001", WhitelistType.APPROVAL);
    }

    @Test
    void testRequiresApprovalAtThreshold() {
        boolean result = budgetApprovalService.requiresApproval(new BigDecimal("10000"), "EMP000001");
        assertFalse(result);
        verify(whitelistService, never()).checkWhitelist(anyString(), any());
    }

    @Test
    void testCanApprove() {
        when(whitelistService.checkWhitelist("EMP000001", WhitelistType.APPROVAL)).thenReturn(true);
        assertTrue(budgetApprovalService.canApprove("EMP000001"));

        when(whitelistService.checkWhitelist("EMP000002", WhitelistType.APPROVAL)).thenReturn(false);
        assertFalse(budgetApprovalService.canApprove("EMP000002"));
    }

    @Test
    void testGetApprovalThreshold() {
        assertEquals(new BigDecimal("10000"), budgetApprovalService.getApprovalThreshold());
    }
}
package com.library.personnel.service;

import com.library.personnel.dto.request.BudgetRequest;
import com.library.personnel.dto.response.BudgetResponse;
import com.library.personnel.dto.response.BudgetSummaryResponse;
import com.library.personnel.entity.Budget;
import com.library.personnel.entity.Employee;
import com.library.personnel.enums.EmployeeStatus;
import com.library.personnel.exception.BusinessException;
import com.library.personnel.exception.ResourceNotFoundException;
import com.library.personnel.repository.BudgetRepository;
import com.library.personnel.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BudgetApprovalService budgetApprovalService;

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService(budgetRepository, employeeRepository, budgetApprovalService);
    }

    @Test
    void testListBudgets() {
        Budget budget = createTestBudget();
        when(budgetRepository.findByEmployeeId("EMP000001")).thenReturn(List.of(budget));

        List<BudgetResponse> result = budgetService.listBudgets("EMP000001");

        assertEquals(1, result.size());
        assertEquals(2025, result.get(0).getYear());
    }

    @Test
    void testCreateBudget() {
        Employee employee = createTestEmployee();
        when(employeeRepository.findByEmployeeIdAndDeletedFalse("EMP000001")).thenReturn(Optional.of(employee));

        BudgetRequest request = new BudgetRequest();
        request.setEmployeeId("EMP000001");
        request.setYear(2025);
        request.setQuarter(1);
        request.setMonth(1);
        request.setBudgetAmount(new BigDecimal("10000.00"));
        request.setUsedAmount(new BigDecimal("5000.00"));

        Budget savedBudget = createTestBudget();
        when(budgetRepository.save(any(Budget.class))).thenReturn(savedBudget);

        BudgetResponse result = budgetService.createBudget(request);

        assertEquals(2025, result.getYear());
        assertEquals(1, result.getQuarter());
        assertEquals(new BigDecimal("10000.00"), result.getBudgetAmount());
    }

    @Test
    void testCreateBudgetEmployeeNotFound() {
        when(employeeRepository.findByEmployeeIdAndDeletedFalse("EMP999")).thenReturn(Optional.empty());

        BudgetRequest request = new BudgetRequest();
        request.setEmployeeId("EMP999");
        request.setYear(2025);

        assertThrows(ResourceNotFoundException.class, () -> budgetService.createBudget(request));
    }

    @Test
    void testGetBudgetSummary() {
        Employee employee = createTestEmployee();
        when(employeeRepository.findByEmployeeIdAndDeletedFalse("EMP000001")).thenReturn(Optional.of(employee));

        Budget annualBudget = createTestBudget();
        annualBudget.setQuarter(null);
        annualBudget.setMonth(null);
        annualBudget.setBudgetAmount(new BigDecimal("120000.00"));
        annualBudget.setUsedAmount(new BigDecimal("45000.00"));
        when(budgetRepository.findAnnualBudget("EMP000001", 2025)).thenReturn(Optional.of(annualBudget));

        for (int q = 1; q <= 4; q++) {
            Budget qBudget = new Budget();
            qBudget.setEmployeeId("EMP000001");
            qBudget.setYear(2025);
            qBudget.setQuarter(q);
            qBudget.setBudgetAmount(new BigDecimal("30000.00"));
            qBudget.setUsedAmount(q == 1 ? new BigDecimal("15000.00") :
                                  q == 2 ? new BigDecimal("10000.00") :
                                  q == 3 ? BigDecimal.ZERO :
                                  new BigDecimal("20000.00"));
            when(budgetRepository.findQuarterlyBudget("EMP000001", 2025, q))
                    .thenReturn(Optional.of(qBudget));
        }

        BudgetSummaryResponse result = budgetService.getBudgetSummary("EMP000001", 2025);

        assertEquals("EMP000001", result.getEmployeeId());
        assertEquals(2025, result.getYear());
        assertEquals(new BigDecimal("120000.00"), result.getTotalBudget());
        assertEquals(new BigDecimal("45000.00"), result.getTotalUsed());
        assertEquals(4, result.getQuarters().size());
        assertEquals(1, result.getQuarters().get(0).getQuarter());
        assertEquals(new BigDecimal("30000.00"), result.getQuarters().get(0).getBudget());
    }

    @Test
    void testUpdateBudgetExceedsThreshold() {
        Budget budget = createTestBudget();
        budget.setUsedAmount(new BigDecimal("1000.00"));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        BudgetRequest request = new BudgetRequest();
        request.setEmployeeId("EMP000001");
        request.setUsedAmount(new BigDecimal("15000.00"));

        when(budgetApprovalService.requiresApproval(new BigDecimal("14000.00"), "EMP000001"))
                .thenReturn(true);

        assertThrows(BusinessException.class, () -> budgetService.updateBudget(1L, request));
    }

    @Test
    void testUpdateBudgetWithinThreshold() {
        Budget budget = createTestBudget();
        budget.setUsedAmount(new BigDecimal("1000.00"));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        BudgetRequest request = new BudgetRequest();
        request.setEmployeeId("EMP000001");
        request.setUsedAmount(new BigDecimal("5000.00"));

        when(budgetApprovalService.requiresApproval(new BigDecimal("4000.00"), "EMP000001"))
                .thenReturn(false);
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        BudgetResponse result = budgetService.updateBudget(1L, request);

        assertNotNull(result);
        verify(budgetRepository).save(any(Budget.class));
    }

    private Budget createTestBudget() {
        Budget budget = new Budget();
        budget.setId(1L);
        budget.setEmployeeId("EMP000001");
        budget.setYear(2025);
        budget.setQuarter(1);
        budget.setMonth(1);
        budget.setBudgetAmount(new BigDecimal("10000.00"));
        budget.setUsedAmount(new BigDecimal("5000.00"));
        budget.setStatus("ACTIVE");
        return budget;
    }

    private Employee createTestEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeId("EMP000001");
        employee.setName("John");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");
        employee.setHireDate(LocalDate.of(2025, 1, 1));
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDeleted(false);
        return employee;
    }
}
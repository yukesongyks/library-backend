package com.library.personnel.service;

import com.library.personnel.dto.request.BudgetRequest;
import com.library.personnel.dto.response.BudgetResponse;
import com.library.personnel.dto.response.BudgetSummaryResponse;
import com.library.personnel.entity.Budget;
import com.library.personnel.entity.Employee;
import com.library.personnel.exception.BusinessException;
import com.library.personnel.exception.ResourceNotFoundException;
import com.library.personnel.repository.BudgetRepository;
import com.library.personnel.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final EmployeeRepository employeeRepository;
    private final BudgetApprovalService budgetApprovalService;

    public BudgetService(BudgetRepository budgetRepository,
                         EmployeeRepository employeeRepository,
                         BudgetApprovalService budgetApprovalService) {
        this.budgetRepository = budgetRepository;
        this.employeeRepository = employeeRepository;
        this.budgetApprovalService = budgetApprovalService;
    }

    public List<BudgetResponse> listBudgets(String employeeId) {
        List<Budget> budgets = budgetRepository.findByEmployeeId(employeeId);
        return budgets.stream().map(BudgetResponse::fromEntity).toList();
    }

    @Transactional
    public BudgetResponse createBudget(BudgetRequest request) {
        employeeRepository.findByEmployeeIdAndDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", request.getEmployeeId()));

        validatePeriod(request.getYear(), request.getQuarter(), request.getMonth());

        Budget budget = new Budget();
        budget.setEmployeeId(request.getEmployeeId());
        budget.setYear(request.getYear());
        budget.setQuarter(request.getQuarter());
        budget.setMonth(request.getMonth());
        budget.setBudgetAmount(request.getBudgetAmount() != null ? request.getBudgetAmount() : BigDecimal.ZERO);
        budget.setUsedAmount(request.getUsedAmount() != null ? request.getUsedAmount() : BigDecimal.ZERO);
        budget.setNote(request.getNote());
        budget = budgetRepository.save(budget);

        cascadeUpdateParent(budget, budget.getUsedAmount(), BigDecimal.ZERO);

        return BudgetResponse.fromEntity(budget);
    }

    @Transactional
    public BudgetResponse updateBudget(Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));

        BigDecimal oldUsedAmount = budget.getUsedAmount();
        BigDecimal newUsedAmount = request.getUsedAmount() != null ? request.getUsedAmount() : oldUsedAmount;

        // Check approval threshold if usedAmount changes significantly
        if (newUsedAmount.compareTo(oldUsedAmount) > 0) {
            BigDecimal change = newUsedAmount.subtract(oldUsedAmount);
            if (budgetApprovalService.requiresApproval(change, request.getEmployeeId())) {
                throw new BusinessException(40010,
                    "Budget change of " + change + " exceeds approval threshold of ¥10,000. " +
                    "Approval from an APPROVAL whitelist member is required.");
            }
        }

        if (request.getBudgetAmount() != null) {
            budget.setBudgetAmount(request.getBudgetAmount());
        }
        budget.setUsedAmount(newUsedAmount);
        if (request.getNote() != null) {
            budget.setNote(request.getNote());
        }
        budget = budgetRepository.save(budget);

        cascadeUpdateParent(budget, newUsedAmount, oldUsedAmount);

        return BudgetResponse.fromEntity(budget);
    }

    public BudgetSummaryResponse getBudgetSummary(String employeeId, Integer year) {
        employeeRepository.findByEmployeeIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));

        Optional<Budget> annualBudget = budgetRepository.findAnnualBudget(employeeId, year);
        BigDecimal totalBudget = annualBudget.map(Budget::getBudgetAmount).orElse(BigDecimal.ZERO);
        BigDecimal totalUsed = annualBudget.map(Budget::getUsedAmount).orElse(BigDecimal.ZERO);

        List<BudgetSummaryResponse.QuarterSummary> quarters = new ArrayList<>();
        for (int q = 1; q <= 4; q++) {
            Optional<Budget> quarterlyBudget = budgetRepository.findQuarterlyBudget(employeeId, year, q);
            BudgetSummaryResponse.QuarterSummary qs = new BudgetSummaryResponse.QuarterSummary();
            qs.setQuarter(q);
            qs.setBudget(quarterlyBudget.map(Budget::getBudgetAmount).orElse(BigDecimal.ZERO));
            qs.setUsed(quarterlyBudget.map(Budget::getUsedAmount).orElse(BigDecimal.ZERO));
            qs.setRemaining(qs.getBudget().subtract(qs.getUsed()));
            quarters.add(qs);
        }

        BudgetSummaryResponse response = new BudgetSummaryResponse();
        response.setEmployeeId(employeeId);
        response.setYear(year);
        response.setTotalBudget(totalBudget);
        response.setTotalUsed(totalUsed);
        response.setTotalRemaining(totalBudget.subtract(totalUsed));
        response.setQuarters(quarters);
        return response;
    }

    private void validatePeriod(Integer year, Integer quarter, Integer month) {
        if (year == null) {
            throw new BusinessException(40001, "Year is required");
        }
        if (quarter != null && (quarter < 1 || quarter > 4)) {
            throw new BusinessException(40001, "Quarter must be between 1 and 4");
        }
        if (month != null && (month < 1 || month > 12)) {
            throw new BusinessException(40001, "Month must be between 1 and 12");
        }
        if (quarter != null && month != null) {
            int expectedQuarter = (month - 1) / 3 + 1;
            if (quarter != expectedQuarter) {
                throw new BusinessException(40001, "Month " + month + " does not belong to quarter " + quarter);
            }
        }
    }

    private void cascadeUpdateParent(Budget budget, BigDecimal newUsedAmount, BigDecimal oldUsedAmount) {
        String employeeId = budget.getEmployeeId();
        Integer year = budget.getYear();
        Integer quarter = budget.getQuarter();
        Integer month = budget.getMonth();

        BigDecimal diff = newUsedAmount.subtract(oldUsedAmount);

        // Update quarterly budget if this is a monthly budget
        if (month != null && quarter != null) {
            Optional<Budget> quarterlyOpt = budgetRepository.findQuarterlyBudget(employeeId, year, quarter);
            if (quarterlyOpt.isPresent()) {
                Budget quarterly = quarterlyOpt.get();
                quarterly.setUsedAmount(quarterly.getUsedAmount().add(diff));
                budgetRepository.save(quarterly);
            }

            // Update annual budget
            Optional<Budget> annualOpt = budgetRepository.findAnnualBudget(employeeId, year);
            if (annualOpt.isPresent()) {
                Budget annual = annualOpt.get();
                annual.setUsedAmount(annual.getUsedAmount().add(diff));
                budgetRepository.save(annual);
            }
        } else if (quarter != null && month == null) {
            // Update annual budget if this is a quarterly budget
            Optional<Budget> annualOpt = budgetRepository.findAnnualBudget(employeeId, year);
            if (annualOpt.isPresent()) {
                Budget annual = annualOpt.get();
                annual.setUsedAmount(annual.getUsedAmount().add(diff));
                budgetRepository.save(annual);
            }
        }
    }
}
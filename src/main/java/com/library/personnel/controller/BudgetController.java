package com.library.personnel.controller;

import com.library.personnel.dto.request.BudgetRequest;
import com.library.personnel.dto.response.ApiResponse;
import com.library.personnel.dto.response.BudgetResponse;
import com.library.personnel.dto.response.BudgetSummaryResponse;
import com.library.personnel.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees/{employeeId}/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> listBudgets(@PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(budgetService.listBudgets(employeeId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @PathVariable String employeeId,
            @Valid @RequestBody BudgetRequest request) {
        request.setEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(budgetService.createBudget(request)));
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable String employeeId,
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetRequest request) {
        request.setEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(budgetService.updateBudget(budgetId, request)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<BudgetSummaryResponse>> getBudgetSummary(
            @PathVariable String employeeId,
            @RequestParam Integer year) {
        return ResponseEntity.ok(ApiResponse.success(budgetService.getBudgetSummary(employeeId, year)));
    }
}
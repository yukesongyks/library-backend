package com.library.personnel.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class BudgetSummaryResponse {

    private String employeeId;
    private Integer year;
    private BigDecimal totalBudget;
    private BigDecimal totalUsed;
    private BigDecimal totalRemaining;
    private List<QuarterSummary> quarters;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getTotalUsed() {
        return totalUsed;
    }

    public void setTotalUsed(BigDecimal totalUsed) {
        this.totalUsed = totalUsed;
    }

    public BigDecimal getTotalRemaining() {
        return totalRemaining;
    }

    public void setTotalRemaining(BigDecimal totalRemaining) {
        this.totalRemaining = totalRemaining;
    }

    public List<QuarterSummary> getQuarters() {
        return quarters;
    }

    public void setQuarters(List<QuarterSummary> quarters) {
        this.quarters = quarters;
    }

    public static class QuarterSummary {
        private Integer quarter;
        private BigDecimal budget;
        private BigDecimal used;
        private BigDecimal remaining;

        public Integer getQuarter() {
            return quarter;
        }

        public void setQuarter(Integer quarter) {
            this.quarter = quarter;
        }

        public BigDecimal getBudget() {
            return budget;
        }

        public void setBudget(BigDecimal budget) {
            this.budget = budget;
        }

        public BigDecimal getUsed() {
            return used;
        }

        public void setUsed(BigDecimal used) {
            this.used = used;
        }

        public BigDecimal getRemaining() {
            return remaining;
        }

        public void setRemaining(BigDecimal remaining) {
            this.remaining = remaining;
        }
    }
}
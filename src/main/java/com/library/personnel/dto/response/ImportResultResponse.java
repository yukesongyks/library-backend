package com.library.personnel.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ImportResultResponse {

    private int successCount;
    private int failureCount;
    private List<ImportFailure> failures = new ArrayList<>();

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<ImportFailure> getFailures() {
        return failures;
    }

    public void setFailures(List<ImportFailure> failures) {
        this.failures = failures;
    }

    public void addFailure(int row, String error) {
        this.failures.add(new ImportFailure(row, error));
        this.failureCount = this.failures.size();
    }

    public void incrementSuccess() {
        this.successCount++;
    }

    public static class ImportFailure {
        private int row;
        private String error;

        public ImportFailure() {}

        public ImportFailure(int row, String error) {
            this.row = row;
            this.error = error;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
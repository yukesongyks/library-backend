package com.library.circulation.dto;

import jakarta.validation.constraints.NotNull;

public class ReturnRequest {

    @NotNull(message = "recordId不能为空")
    private Long recordId;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
}

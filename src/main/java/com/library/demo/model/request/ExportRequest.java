package com.library.demo.model.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ExportRequest {
    @NotBlank(message = "type is required")
    private String type; // helloworld | hash | bubble-sort
    private List<Long> recordIds; // optional, empty = export all

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<Long> getRecordIds() { return recordIds; }
    public void setRecordIds(List<Long> recordIds) { this.recordIds = recordIds; }
}

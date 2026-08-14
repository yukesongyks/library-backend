package com.library.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ExportRequest {
    @NotNull(message = "type is required")
    private String type; // HELLOWORLD|HASH|BUBBLE_SORT
    private List<Long> recordIds;
}

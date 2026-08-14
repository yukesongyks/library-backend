package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;
import java.util.Set;

@Data
public class ExportRequest {
    @NotBlank
    private String type;

    @NotNull
    private Map<String, Object> data;

    private String format = "csv";

    private static final Set<String> VALID_TYPES = Set.of("helloworld", "hash", "bubblesort");
    private static final Set<String> VALID_FORMATS = Set.of("csv", "xlsx");

    public void validate() {
        if (!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException("导出类型仅支持: " + String.join(", ", VALID_TYPES));
        }
        if (!VALID_FORMATS.contains(format)) {
            throw new IllegalArgumentException("导出格式仅支持 csv 或 xlsx");
        }
    }
}
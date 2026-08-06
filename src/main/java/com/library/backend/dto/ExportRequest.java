package com.library.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExportRequest {
    @NotBlank(message = "type 不能为空")
    private String type;

    @NotBlank(message = "format 不能为空")
    private String format;

    @NotNull(message = "data 不能为 null")
    private Object data;
}

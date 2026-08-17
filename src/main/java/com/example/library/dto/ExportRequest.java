package com.example.library.dto;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Request DTO for export API.
 */
public class ExportRequest {

    @NotBlank(message = "Type must not be blank")
    private String type;

    @NotNull(message = "Data must not be null")
    private Map<String, Object> data;

    private String format = "json";

    public ExportRequest() {
    }

    public ExportRequest(String type, Map<String, Object> data, String format) {
        this.type = type;
        this.data = data;
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
package com.library.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> DemoResponse<T> success(T data) {
        return new DemoResponse<>(200, "success", data);
    }

    public static <T> DemoResponse<T> error(int code, String message) {
        return new DemoResponse<>(code, message, null);
    }
}

package com.library.backend.common;

/**
 * Unified response body (design spec §4.1).
 *
 * <pre>{@code
 * public record Result<T>(int code, String message, T data) {
 *     public static <T> Result<T> success(T data) { return new Result<>(0, "success", data); }
 *     public static <T> Result<T> error(int code, String message) { return new Result<>(code, message, null); }
 * }
 * }</pre>
 *
 * @param code    0 = success; 40001 param validation; 40002 business rule; 50000 system internal
 * @param message human-readable message
 * @param data    payload, null on error
 */
public record Result<T>(int code, String message, T data) {

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}

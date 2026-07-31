package com.library.backend.common;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * Global exception handler (design spec §5.2.1).
 *
 * <p>Layered handlers returning {@link Result#error(int, String)} (HTTP 200 +
 * nonzero code) for business endpoints. The {@link IOException} handler is
 * specific to the CSV export path and returns HTTP 500 (§5.2.4).
 *
 * <table>
 *   <tr><th>Exception</th><th>Handler</th><th>Return</th></tr>
 *   <tr><td>MethodArgumentNotValidException (@Valid)</td><td>param validation</td><td>Result.error(40001, field error desc)</td></tr>
 *   <tr><td>IllegalArgumentException (business precheck)</td><td>business</td><td>Result.error(40002, reason)</td></tr>
 *   <tr><td>ConstraintViolationException (query @Validated)</td><td>param validation</td><td>Result.error(40001, desc)</td></tr>
 *   <tr><td>IOException (CSV export)</td><td>export</td><td>HTTP 500 + Result.error(50000, "导出失败，请稍后重试")</td></tr>
 *   <tr><td>Exception (fallback)</td><td>unknown</td><td>Result.error(50000, "系统繁忙，请稍后重试")</td></tr>
 * </table>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** @Valid body validation failure → 40001. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String desc = fieldError != null ? fieldError.getField() + " " + fieldError.getDefaultMessage()
                : "参数校验失败";
        return ResponseEntity.ok(Result.error(40001, desc));
    }

    /** Business precheck failure → 40002. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.ok(Result.error(40002, ex.getMessage()));
    }

    /** Query-param @Validated failure (e.g. illegal export tab) → 40001. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String desc = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("参数校验失败");
        return ResponseEntity.ok(Result.error(40001, desc));
    }

    /** CSV export IOException → HTTP 500 + 50000 (§5.2.4). */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Result<Void>> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(50000, "导出失败，请稍后重试"));
    }

    /** Fallback for any uncaught exception → 50000. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception ex) {
        return ResponseEntity.ok(Result.error(50000, "系统繁忙，请稍后重试"));
    }
}

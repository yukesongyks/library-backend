package com.library.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 全局异常处理器。
 *
 * <p>兜底矩阵（proposal.md + design.md）：
 * <ul>
 *   <li>{@code IllegalArgumentException} → 400 {@code {"error": msg}}
 *       （参数校验/业务异常：hash 不支持算法、export 无效 type、analytics 无效 dimension/chartType）</li>
 *   <li>{@code MethodArgumentNotValidException} → 400 {@code {"error": fieldErrorMsg}}
 *       （{@code @Valid} 校验失败，提取首个字段错误消息，如 "text must not be empty"）</li>
 *   <li>{@code NoSuchAlgorithmException} → 500 {@code {"error": "internal error"}}
 *       （哈希算法兜底，理论不可达）</li>
 *   <li>{@code DataAccessException} → 500 {@code {"error": "internal error"}}</li>
 * </ul>
 * 所有响应体统一 {@code {"error": "..."}} 结构，与 spec 场景一致。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validationError(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "validation error";
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<Map<String, String>> algoError(NoSuchAlgorithmException e) {
        log.error("哈希算法异常: {}", e.getMessage());
        return ResponseEntity.status(500).body(Map.of("error", "internal error"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> dbError(DataAccessException e) {
        log.error("数据库异常", e);
        return ResponseEntity.status(500).body(Map.of("error", "internal error"));
    }
}

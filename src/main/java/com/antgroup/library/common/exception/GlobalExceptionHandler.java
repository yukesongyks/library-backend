package com.antgroup.library.common.exception;

import com.antgroup.library.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException ex) {
        log.warn("业务异常 code={} msg={}", ex.getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(Result.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败 {}", message);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(ErrorCode.COMMON_001.getCode(), message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBind(BindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败 {}", message);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(ErrorCode.COMMON_001.getCode(), message));
    }

    /**
     * 捕获数据库唯一约束冲突（如 ISBN/手机号重复），转为友好业务提示。
     * <p>并发场景下 select-then-insert 的 TOCTOU 窗口由 DB 唯一约束兜底，
     * 此处将 DuplicateKeyException 转为 COMMON_001 级别提示，避免 500 错误。</p>
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKey(DuplicateKeyException ex) {
        log.warn("唯一约束冲突 {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(ErrorCode.COMMON_002.getCode(), ErrorCode.COMMON_002.getMessage()));
    }

    /**
     * 捕获其他数据库访问异常，避免暴露底层 SQL 细节。
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDataAccess(DataAccessException ex) {
        log.error("数据库访问异常", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(ErrorCode.COMMON_999.getCode(), ErrorCode.COMMON_999.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnexpected(Exception ex) {
        log.error("系统异常", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(ErrorCode.COMMON_999.getCode(), ErrorCode.COMMON_999.getMessage()));
    }
}

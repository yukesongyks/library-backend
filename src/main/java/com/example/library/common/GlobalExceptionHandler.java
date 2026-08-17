package com.example.library.common;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 * Catches validation, illegal argument, and unexpected exceptions,
 * returning standardized ApiResult error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation exceptions from {@code @Valid} annotated request bodies.
     *
     * @param ex the validation exception
     * @return ApiResult with 400 status and the first field error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Validation failed";
        return ApiResult.error(400, message);
    }

    /**
     * Handles illegal argument exceptions thrown by service or controller logic.
     *
     * @param ex the illegal argument exception
     * @return ApiResult with 400 status and the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResult.error(400, ex.getMessage());
    }

    /**
     * Handles unexpected exceptions as a catch-all for unhandled errors.
     *
     * @param ex the unexpected exception
     * @return ApiResult with 500 status and generic error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        return ApiResult.error(500, "Internal server error");
    }
}
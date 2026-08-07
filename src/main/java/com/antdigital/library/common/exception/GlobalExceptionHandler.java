package com.antdigital.library.common.exception;

import com.antdigital.library.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 *
 * <p>将异常转化为统一的 ApiResponse 格式，包含 errorCode / errorMessage / userTip。</p>
 *
 * @author library-backend
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常。
     *
     * @param e 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(ServiceException.class)
    public ApiResponse<Void> handleServiceException(ServiceException e) {
        logger.warn("业务异常, errorCode: {}, message: {}", e.getErrorCode(), e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getErrorCode(), e.getMessage(), e.getUserTip());
    }

    /**
     * 处理参数校验异常。
     *
     * @param e 参数校验异常
     * @return 统一错误响应
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidationException(Exception e) {
        logger.warn("参数校验异常: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                ErrorCodeEnum.PARAM_INVALID.getCode(),
                ErrorCodeEnum.PARAM_INVALID.getMessage(),
                "请求参数不合法，请检查后重试");
    }

    /**
     * 处理必填参数缺失异常。
     *
     * @param e 参数缺失异常
     * @return 统一错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<Void> handleMissingParam(MissingServletRequestParameterException e) {
        logger.warn("必填参数缺失: {}", e.getParameterName());
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(),
                ErrorCodeEnum.PARAM_EMPTY.getCode(),
                ErrorCodeEnum.PARAM_EMPTY.getMessage() + ": " + e.getParameterName(),
                "缺少必填参数: " + e.getParameterName());
    }

    /**
     * 处理未知系统异常。
     *
     * @param e 系统异常
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleUnexpectedException(Exception e) {
        logger.error("系统异常, errorMessage: {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorCodeEnum.SYSTEM_ERROR.getCode(),
                ErrorCodeEnum.SYSTEM_ERROR.getMessage(),
                "系统繁忙，请稍后重试");
    }
}

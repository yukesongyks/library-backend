package com.library.common;

/**
 * 业务异常，携带 HTTP 状态码与错误消息。
 */
public class BusinessException extends RuntimeException {

    private final int statusCode;

    public BusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

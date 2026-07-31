package com.antgroup.library.common.exception;

import lombok.Getter;

/**
 * 业务异常（设计文档各模块错误码 R 规则）。
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }
}

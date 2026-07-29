package com.library.common.exception;

import lombok.Getter;

/**
 * 业务异常，携带错误码与提示信息。
 *
 * @author DTCoder
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private final String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }
}

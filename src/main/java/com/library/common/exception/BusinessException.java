package com.library.common.exception;

import com.library.common.enums.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * <p>
 * 用于在业务层抛出可预期的业务错误, 由全局异常处理器统一捕获并转换返回.
 *
 * @author library-team
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}

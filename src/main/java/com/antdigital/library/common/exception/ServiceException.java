package com.antdigital.library.common.exception;

/**
 * 业务服务异常。
 *
 * <p>遵循自定义异常规范：unchecked，携带错误码与用户提示。</p>
 *
 * @author library-backend
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    private final String userTip;

    public ServiceException(ErrorCodeEnum errorCodeEnum, String userTip) {
        super(errorCodeEnum.getMessage());
        this.errorCode = errorCodeEnum.getCode();
        this.userTip = userTip;
    }

    public ServiceException(ErrorCodeEnum errorCodeEnum, String detailMessage, String userTip) {
        super(errorCodeEnum.getMessage() + ": " + detailMessage);
        this.errorCode = errorCodeEnum.getCode();
        this.userTip = userTip;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserTip() {
        return userTip;
    }
}

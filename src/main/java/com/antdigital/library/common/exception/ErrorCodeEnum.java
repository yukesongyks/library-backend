package com.antdigital.library.common.exception;

/**
 * 业务异常错误码枚举。
 *
 * <p>遵循错误码规范：5位字符串，来源(A/B/C) + 四位数字编号。</p>
 *
 * @author library-backend
 */
public enum ErrorCodeEnum {

    /** 用户端参数错误 */
    PARAM_INVALID("A0001", "参数校验失败"),

    /** 用户输入为空 */
    PARAM_EMPTY("A0002", "必填参数为空"),

    /** 系统执行出错 */
    SYSTEM_ERROR("B0001", "系统内部错误"),

    /** 系统计算异常 */
    COMPUTE_ERROR("B0002", "计算处理异常"),

    /** 不支持的类型 */
    UNSUPPORTED_TYPE("A0003", "不支持的操作类型");

    private final String code;

    private final String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

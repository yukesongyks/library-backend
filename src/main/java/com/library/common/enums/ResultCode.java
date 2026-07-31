package com.library.common.enums;

/**
 * 系统错误码枚举
 * <p>
 * 错误码格式: 五位数字, 前两位表示模块, 后三位表示具体错误序号
 * <ul>
 *     <li>10xxx: 通用错误</li>
 *     <li>11xxx: 图书模块错误</li>
 * </ul>
 *
 * @author library-team
 */
public enum ResultCode {

    /* 通用错误 10xxx */
    SUCCESS(10000, "成功"),
    PARAM_INVALID(10001, "参数校验失败"),
    PARAM_MISSING(10002, "必填参数缺失"),
    SYSTEM_ERROR(10999, "系统异常"),

    /* 图书模块错误 11xxx */
    BOOK_NOT_FOUND(11001, "图书不存在"),
    BOOK_ISBN_DUPLICATED(11002, "ISBN已存在"),
    BOOK_STOCK_INVALID(11003, "库存数量不能为负数");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

package com.library.backend.common;

/**
 * 错误码枚举
 */
public enum ResultCode {

    SUCCESS(0, "success"),

    DEMO_0001(1001, "系统内部异常"),
    DEMO_0002(1002, "input 不能为空"),
    DEMO_0003(1003, "input 长度超过最大限制"),
    DEMO_0004(1004, "不支持的 algorithm 值"),
    DEMO_0005(1005, "numbers 不能为空"),
    DEMO_0006(1006, "numbers 长度超过最大限制"),
    DEMO_0007(1007, "numbers 元素超出值域范围");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

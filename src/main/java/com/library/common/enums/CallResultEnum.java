package com.library.common.enums;

/**
 * 调用结果枚举
 */
public enum CallResultEnum {

    SUCCESS("SUCCESS", "调用成功"),
    FAIL("FAIL", "调用失败");

    private final String code;
    private final String desc;

    CallResultEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

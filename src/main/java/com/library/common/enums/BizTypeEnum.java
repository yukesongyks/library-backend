package com.library.common.enums;

/**
 * 业务类型枚举
 */
public enum BizTypeEnum {

    HELLOWORLD("HELLOWORLD", "helloworld接口"),
    HASH("HASH", "哈希算法接口"),
    BUBBLE_SORT("BUBBLE_SORT", "冒泡排序接口");

    private final String code;
    private final String desc;

    BizTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static BizTypeEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (BizTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}

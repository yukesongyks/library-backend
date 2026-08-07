package com.antfin.library.common.enums;

/**
 * 算法类型枚举
 */
public enum AlgorithmTypeEnum {

    HELLO_WORLD("HELLO_WORLD", "HelloWorld"),
    HASH("HASH", "哈希算法"),
    BUBBLE_SORT("BUBBLE_SORT", "冒泡排序");

    private final String code;
    private final String desc;

    AlgorithmTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AlgorithmTypeEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AlgorithmTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}

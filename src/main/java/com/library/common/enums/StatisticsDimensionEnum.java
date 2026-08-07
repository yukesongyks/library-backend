package com.library.common.enums;

/**
 * 统计维度枚举
 */
public enum StatisticsDimensionEnum {

    CALLER_TYPE("CALLER_TYPE", "人员类型"),
    CALLER_LEVEL("CALLER_LEVEL", "人员层级"),
    CALLER_DEPT("CALLER_DEPT", "人员部门");

    private final String code;
    private final String desc;

    StatisticsDimensionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static StatisticsDimensionEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (StatisticsDimensionEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}

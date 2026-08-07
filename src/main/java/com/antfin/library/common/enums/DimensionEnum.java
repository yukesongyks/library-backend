package com.antfin.library.common.enums;

/**
 * 报表聚合维度枚举
 */
public enum DimensionEnum {

    USER_TYPE("USER_TYPE", "人员类型"),
    USER_LEVEL("USER_LEVEL", "人员层级"),
    DEPARTMENT("DEPARTMENT", "人员部门");

    private final String code;
    private final String desc;

    DimensionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DimensionEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (DimensionEnum e : values()) {
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

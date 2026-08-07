package com.library.common.enums;

/**
 * 哈希算法枚举
 */
public enum HashAlgorithmEnum {

    SHA_256("SHA-256", "SHA-256算法"),
    MD5("MD5", "MD5算法");

    private final String code;
    private final String desc;

    HashAlgorithmEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static HashAlgorithmEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        String normalized = code.replace("_", "-").toUpperCase();
        for (HashAlgorithmEnum e : values()) {
            if (e.code.equalsIgnoreCase(normalized) || e.name().equalsIgnoreCase(code)) {
                return e;
            }
        }
        return null;
    }
}

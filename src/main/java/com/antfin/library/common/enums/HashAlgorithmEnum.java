package com.antfin.library.common.enums;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希算法枚举
 */
public enum HashAlgorithmEnum {

    MD5("MD5", "MD5摘要"),
    SHA256("SHA-256", "SHA256摘要"),
    SHA512("SHA-512", "SHA512摘要");

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

    public String getJceName() {
        return code;
    }

    public static HashAlgorithmEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (HashAlgorithmEnum e : values()) {
            if (e.code.equalsIgnoreCase(code)) {
                return e;
            }
        }
        return null;
    }

    public static HashAlgorithmEnum fromCodeOrDefault(String code) {
        HashAlgorithmEnum e = fromCode(code);
        return e != null ? e : SHA256;
    }

    public MessageDigest newMessageDigest() {
        try {
            return MessageDigest.getInstance(getJceName());
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("不支持的哈希算法: " + getJceName(), ex);
        }
    }
}

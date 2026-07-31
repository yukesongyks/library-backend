package com.library.backend.demo.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 哈希算法枚举
 */
public enum HashAlgorithmEnum {

    SHA256,
    MD5;

    private static final Logger log = LoggerFactory.getLogger(HashAlgorithmEnum.class);

    /**
     * 大小写不敏感解析
     *
     * @param value 算法名称
     * @return 枚举值，非法返回 null
     */
    public static HashAlgorithmEnum fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return SHA256;
        }
        try {
            return HashAlgorithmEnum.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.debug("非法哈希算法名称: {}", value, e);
            return null;
        }
    }
}

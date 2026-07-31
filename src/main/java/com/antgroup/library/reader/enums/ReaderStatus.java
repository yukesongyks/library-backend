package com.antgroup.library.reader.enums;

/**
 * 读者状态（设计文档 5.2.1.2）。
 */
public enum ReaderStatus {

    ACTIVE("ACTIVE", "正常"),
    DISABLED("DISABLED", "停用");

    private final String value;
    private final String desc;

    ReaderStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        for (ReaderStatus status : values()) {
            if (status.value.equals(value)) {
                return true;
            }
        }
        return false;
    }
}

package com.antgroup.library.book.enums;

/**
 * 图书逻辑删除标记（设计文档 5.1.1.2）。
 */
public enum BookDeleteFlag {

    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final int value;
    private final String desc;

    BookDeleteFlag(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}

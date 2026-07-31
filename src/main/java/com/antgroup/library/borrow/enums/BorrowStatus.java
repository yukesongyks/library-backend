package com.antgroup.library.borrow.enums;

/**
 * 借阅状态（设计文档 5.3.1.2 / 5.3.1.3 状态机）。
 */
public enum BorrowStatus {

    BORROWING("BORROWING", "借阅中"),
    RETURNED("RETURNED", "已归还"),
    OVERDUE("OVERDUE", "逾期中");

    private final String value;
    private final String desc;

    BorrowStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean canReturn(String status) {
        return BORROWING.value.equals(status) || OVERDUE.value.equals(status);
    }
}

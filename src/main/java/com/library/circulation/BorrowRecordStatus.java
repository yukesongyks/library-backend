package com.library.circulation;

/**
 * 借阅记录状态枚举，对齐 design.md。
 * ACTIVE: 借阅中（在册）
 * RETURNED: 已归还
 * OVERDUE: 逾期
 */
public enum BorrowRecordStatus {
    ACTIVE,
    RETURNED,
    OVERDUE
}

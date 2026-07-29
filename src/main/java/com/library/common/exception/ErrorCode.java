package com.library.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举，格式：{MODULE}_{SEQ}。
 *
 * @author DTCoder
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== 用户与权限模块 AUTH =====
    AUTH_001("AUTH_001", "用户名或密码错误"),
    AUTH_002("AUTH_002", "账号已冻结"),
    AUTH_003("AUTH_003", "未登录"),
    AUTH_004("AUTH_004", "无权限"),
    AUTH_005("AUTH_005", "无权操作他人记录"),

    // ===== 图书管理模块 BOOK =====
    BOOK_001("BOOK_001", "ISBN已存在"),
    BOOK_002("BOOK_002", "分类不存在"),
    BOOK_003("BOOK_003", "库存不能为负数"),
    BOOK_004("BOOK_004", "分页参数非法"),
    BOOK_005("BOOK_005", "图书不存在"),
    BOOK_006("BOOK_006", "图书已删除"),

    // ===== 读者管理模块 READER =====
    READER_001("READER_001", "用户名已存在"),
    READER_002("READER_002", "读者编号已存在"),
    READER_003("READER_003", "读者不存在"),

    // ===== 借阅管理模块 BORROW =====
    BORROW_001("BORROW_001", "图书不存在或已删除"),
    BORROW_002("BORROW_002", "库存不足"),
    BORROW_003("BORROW_003", "读者存在逾期未归还记录"),
    BORROW_004("BORROW_004", "超过最大在借数量"),
    BORROW_005("BORROW_005", "借阅记录不存在"),
    BORROW_006("BORROW_006", "该记录非在借状态"),
    BORROW_007("BORROW_007", "无权操作他人借阅记录"),

    // ===== 系统通用 =====
    SYSTEM_ERROR("SYSTEM_ERROR", "系统繁忙，请稍后重试");

    /** 错误码 */
    private final String code;

    /** 提示信息 */
    private final String msg;
}

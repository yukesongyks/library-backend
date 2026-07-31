package com.antgroup.library.common.exception;

import lombok.Getter;

/**
 * 错误码枚举（设计文档 5.0.2，格式 {MODULE}_{SEQ}）。
 */
@Getter
public enum ErrorCode {

    // 公共错误码
    COMMON_001("COMMON_001", "参数校验失败"),
    COMMON_002("COMMON_002", "数据已存在，请勿重复提交"),
    COMMON_999("COMMON_999", "系统异常"),

    // 图书模块
    BOOK_001("BOOK_001", "图书不存在"),
    BOOK_002("BOOK_002", "ISBN已存在"),
    BOOK_003("BOOK_003", "图书有未归还借阅记录，禁止修改ISBN"),
    BOOK_004("BOOK_004", "图书有未归还借阅记录，禁止删除"),
    BOOK_005("BOOK_005", "图书库存不足"),

    // 读者模块
    READER_001("READER_001", "读者不存在"),
    READER_002("READER_002", "手机号已存在"),
    READER_003("READER_003", "读者状态不合法"),
    READER_004("READER_004", "读者有未归还借阅记录，禁止删除"),
    READER_005("READER_005", "读者状态异常"),

    // 借阅模块
    BORROW_001("BORROW_001", "读者已达借阅上限"),
    BORROW_002("BORROW_002", "借阅记录不存在"),
    BORROW_003("BORROW_003", "借阅记录状态非借阅中/逾期，无法归还");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

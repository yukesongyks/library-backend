package com.library.common;

/**
 * 业务状态码常量
 *
 * @author library
 */
public final class ResultCode {

    /** 成功 */
    public static final int SUCCESS = 200;
    /** 失败 */
    public static final int FAIL = 500;

    /** 参数错误 */
    public static final int PARAM_ERROR = 400;
    /** 未授权 */
    public static final int UNAUTHORIZED = 401;
    /** 禁止访问 */
    public static final int FORBIDDEN = 403;
    /** 资源不存在 */
    public static final int NOT_FOUND = 404;

    /** 库存不足 */
    public static final int STOCK_NOT_ENOUGH = 10001;
    /** 图书不存在 */
    public static final int BOOK_NOT_FOUND = 10002;
    /** 读者不存在 */
    public static final int READER_NOT_FOUND = 10003;
    /** 借阅记录不存在 */
    public static final int BORROW_RECORD_NOT_FOUND = 10004;
    /** 重复借阅 */
    public static final int DUPLICATE_BORROW = 10005;
    /** 已归还 */
    public static final int ALREADY_RETURNED = 10006;

    private ResultCode() {
    }
}

package com.library.common;

/**
 * 业务常量
 *
 * @author library
 */
public final class BusinessConstants {

    /** 默认借阅期限天数 */
    public static final int DEFAULT_BORROW_DAYS = 30;

    /** 借阅状态: 借阅中 */
    public static final String BORROW_STATUS_BORROWED = "BORROWED";

    /** 借阅状态: 已归还 */
    public static final String BORROW_STATUS_RETURNED = "RETURNED";

    /** 借阅状态: 逾期 */
    public static final String BORROW_STATUS_OVERDUE = "OVERDUE";

    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;

    /** 默认页大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    private BusinessConstants() {
    }
}

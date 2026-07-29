package com.library.common.constant;

/**
 * 系统常量定义。
 *
 * @author DTCoder
 */
public final class LibraryConstants {

    private LibraryConstants() {
    }

    /** 借阅期限默认天数 */
    public static final int BORROW_PERIOD_DAYS = 30;

    /** 单读者最大在借数量 */
    public static final int MAX_BORROW_COUNT = 5;

    /** JWT Token 过期小时数 */
    public static final int JWT_EXPIRE_HOURS = 24;

    /** 请求头 Authorization */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /** Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 请求属性 - 当前用户ID */
    public static final String ATTR_USER_ID = "currentUserId";

    /** 请求属性 - 当前用户角色 */
    public static final String ATTR_USER_ROLE = "currentUserRole";

    /** 角色 - 管理员 */
    public static final String ROLE_ADMIN = "ADMIN";

    /** 角色 - 读者 */
    public static final String ROLE_READER = "READER";

    /** 借阅状态 - 在借中 */
    public static final String STATUS_BORROWING = "BORROWING";

    /** 借阅状态 - 已归还 */
    public static final String STATUS_RETURNED = "RETURNED";

    /** 借阅状态 - 已逾期 */
    public static final String STATUS_OVERDUE = "OVERDUE";

    /** 逻辑删除 - 未删除 */
    public static final int NOT_DELETED = 0;

    /** 逻辑删除 - 已删除 */
    public static final int DELETED = 1;
}

package com.library.common.context;

import com.library.common.constant.LibraryConstants;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 当前登录用户上下文（基于请求属性，线程隔离）。
 *
 * @author DTCoder
 */
public final class UserContext {

    private UserContext() {
    }

    /**
     * 设置当前用户ID到请求属性。
     *
     * @param request HTTP请求
     * @param userId 用户ID
     */
    public static void setUserId(HttpServletRequest request, Long userId) {
        request.setAttribute(LibraryConstants.ATTR_USER_ID, userId);
    }

    /**
     * 获取当前用户ID。
     *
     * @param request HTTP请求
     * @return 用户ID，未登录返回null
     */
    public static Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(LibraryConstants.ATTR_USER_ID);
    }

    /**
     * 设置当前用户角色到请求属性。
     *
     * @param request HTTP请求
     * @param role    角色
     */
    public static void setRole(HttpServletRequest request, String role) {
        request.setAttribute(LibraryConstants.ATTR_USER_ROLE, role);
    }

    /**
     * 获取当前用户角色。
     *
     * @param request HTTP请求
     * @return 角色，未登录返回null
     */
    public static String getRole(HttpServletRequest request) {
        return (String) request.getAttribute(LibraryConstants.ATTR_USER_ROLE);
    }
}

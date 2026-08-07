package com.antfin.library.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具：从请求头获取当前用户ID
 */
public final class UserContextUtil {

    private static final String USER_ID_HEADER = "X-User-Id";

    private UserContextUtil() {
    }

    /**
     * 从请求头 X-User-Id 获取当前用户ID
     *
     * @return 用户ID，获取失败时返回 "anonymous"
     */
    public static String getCurrentUserId() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "anonymous";
        }
        HttpServletRequest request = attrs.getRequest();
        String userId = request.getHeader(USER_ID_HEADER);
        if (userId == null || userId.trim().isEmpty()) {
            return "anonymous";
        }
        return userId.trim();
    }
}

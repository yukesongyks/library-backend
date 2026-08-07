package com.library.common.context;

import com.library.common.model.CallerContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求上下文辅助类（集成接口 I01）
 * 从请求头获取统一登录/网关注入的调用人身份及维度信息。
 */
@Component
public class RequestContextHelper {

    private static final String HEADER_CALLER_ID = "x-caller-id";
    private static final String HEADER_CALLER_NAME = "x-caller-name";
    private static final String HEADER_CALLER_TYPE = "x-caller-type";
    private static final String HEADER_CALLER_LEVEL = "x-caller-level";
    private static final String HEADER_CALLER_DEPT = "x-caller-dept";

    /**
     * 从请求头获取调用人上下文
     */
    public CallerContext getCallerContext(HttpServletRequest request) {
        CallerContext ctx = new CallerContext();
        if (request == null) {
            return ctx;
        }
        ctx.setCallerId(getHeader(request, HEADER_CALLER_ID, "anonymous"));
        ctx.setCallerName(getHeader(request, HEADER_CALLER_NAME, "匿名用户"));
        ctx.setCallerType(getHeader(request, HEADER_CALLER_TYPE, "未知"));
        ctx.setCallerLevel(getHeader(request, HEADER_CALLER_LEVEL, "未知"));
        ctx.setCallerDept(getHeader(request, HEADER_CALLER_DEPT, "未知"));
        return ctx;
    }

    private String getHeader(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getHeader(name);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
    }
}

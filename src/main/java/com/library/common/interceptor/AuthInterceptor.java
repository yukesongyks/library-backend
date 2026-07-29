package com.library.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.constant.LibraryConstants;
import com.library.common.context.UserContext;
import com.library.common.exception.BizException;
import com.library.common.exception.ErrorCode;
import com.library.common.jwt.JwtUtil;
import com.library.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 认证与权限拦截器。
 * <p>
 * 白名单：/api/auth/login。
 * /api/admin/** 需 ADMIN 角色；其余 /api/** 需登录态。
 * </p>
 *
 * @author DTCoder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        // 白名单：登录接口
        if (uri.equals("/api/auth/login")) {
            return true;
        }

        // 非 /api 前缀放行（静态资源等）
        if (!uri.startsWith("/api/")) {
            return true;
        }

        // 校验 Token
        String authHeader = request.getHeader(LibraryConstants.HEADER_AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(LibraryConstants.TOKEN_PREFIX)) {
            return writeFail(response, ErrorCode.AUTH_003);
        }

        String token = authHeader.substring(LibraryConstants.TOKEN_PREFIX.length());
        Long userId;
        String role;
        try {
            userId = jwtUtil.parseUserId(token);
            role = jwtUtil.parseRole(token);
        } catch (BizException e) {
            return writeFail(response, ErrorCode.AUTH_003);
        }

        // 垂直权限校验：/api/admin/** 需 ADMIN
        if (uri.startsWith("/api/admin/") && !LibraryConstants.ROLE_ADMIN.equals(role)) {
            return writeFail(response, ErrorCode.AUTH_004);
        }

        // 设置当前用户上下文
        UserContext.setUserId(request, userId);
        UserContext.setRole(request, role);
        return true;
    }

    /**
     * 写入失败响应。
     *
     * @param response   HTTP响应
     * @param errorCode  错误码
     * @return false 终止后续处理
     */
    private boolean writeFail(HttpServletResponse response, ErrorCode errorCode) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            String json = objectMapper.writeValueAsString(
                    Result.fail(errorCode.getCode(), errorCode.getMsg()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error("写入拦截器失败响应异常", e);
        }
        return false;
    }
}

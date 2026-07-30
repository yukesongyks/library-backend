package com.library.auth;

/**
 * JWT 解析后的 Principal，供 Controller 获取当前用户信息。
 */
public record JwtPrincipal(Long userId, String username, String role) {
}

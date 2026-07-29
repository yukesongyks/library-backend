package com.library.common.jwt;

import com.library.common.constant.LibraryConstants;
import com.library.common.exception.BizException;
import com.library.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类，负责 Token 签发与校验。
 *
 * @author DTCoder
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${library.jwt.secret}")
    private String secret;

    @Value("${library.jwt.expire-hours:" + LibraryConstants.JWT_EXPIRE_HOURS + "}")
    private int expireHours;

    private SecretKey key;

    /**
     * 初始化签名密钥。
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发 JWT Token。
     *
     * @param userId 用户ID
     * @param role   角色
     * @return JWT Token
     */
    public String generateToken(Long userId, String role) {
        long now = System.currentTimeMillis();
        long expiry = now + (long) expireHours * 3600_000L;
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date(now))
                .expiration(new Date(expiry))
                .signWith(key)
                .compact();
    }

    /**
     * 解析并校验 Token，返回用户ID。
     *
     * @param token JWT Token（不含 Bearer 前缀）
     * @return 用户ID
     * @throws BizException Token无效或过期
     */
    public Long parseUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 解析并校验 Token，返回角色。
     *
     * @param token JWT Token（不含 Bearer 前缀）
     * @return 角色
     * @throws BizException Token无效或过期
     */
    public String parseRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.warn("JWT解析失败: {}", e.getMessage());
            throw new BizException(ErrorCode.AUTH_003);
        }
    }
}

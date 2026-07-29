package com.library.auth.service.impl;

import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.LoginResult;
import com.library.auth.entity.SysUserDO;
import com.library.auth.mapper.SysUserMapper;
import com.library.auth.service.AuthService;
import com.library.common.constant.LibraryConstants;
import com.library.common.exception.BizException;
import com.library.common.exception.ErrorCode;
import com.library.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现。
 *
 * @author DTCoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResult login(LoginRequest req) {
        // R03: 用户必须存在
        SysUserDO user = sysUserMapper.selectByUsername(req.getUsername());
        if (user == null) {
            throw new BizException(ErrorCode.AUTH_001);
        }

        // R04: 密码BCrypt校验
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.AUTH_001);
        }

        // R05: 账号状态为ACTIVE
        if (!LibraryConstants.STATUS_ACTIVE.equals(user.getStatus())) {
            throw new BizException(ErrorCode.AUTH_002);
        }

        // 签发JWT
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        return new LoginResult(token, user.getRole());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSysUser(String username, String password, String role) {
        SysUserDO user = new SysUserDO();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(LibraryConstants.STATUS_ACTIVE);
        sysUserMapper.insert(user);
        return user.getId();
    }

    @Override
    public SysUserDO getById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return sysUserMapper.selectByUsername(username) != null;
    }
}

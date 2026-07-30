package com.library.auth;

import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.LoginResponse;
import com.library.auth.dto.MeResponse;
import com.library.common.ApiResponse;
import com.library.common.BusinessException;
import com.library.reader.Reader;
import com.library.reader.ReaderRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器：登录、当前用户信息。
 * 契约对齐 design.md：
 *   POST /api/auth/login（public）
 *   GET  /api/auth/me（any authenticated）
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ReaderRepository readerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(ReaderRepository readerRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.readerRepository = readerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        Reader reader = readerRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if (!Boolean.TRUE.equals(reader.getEnabled())) {
            throw new BusinessException(403, "账号已禁用");
        }

        if (!passwordEncoder.matches(req.getPassword(), reader.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(
                reader.getUsername(),
                reader.getRole().name(),
                reader.getId()
        );

        return ApiResponse.ok(new LoginResponse(token, reader.getRole().name(), reader.getUsername()));
    }

    @GetMapping("/me")
    public ApiResponse<MeResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtPrincipal principal)) {
            throw new BusinessException(401, "未认证");
        }
        return ApiResponse.ok(new MeResponse(
                principal.userId(),
                principal.username(),
                principal.role()
        ));
    }
}

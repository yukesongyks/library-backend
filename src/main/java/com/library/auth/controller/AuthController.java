package com.library.auth.controller;

import com.library.auth.dto.CurrentUserVO;
import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.LoginResult;
import com.library.auth.entity.SysUserDO;
import com.library.auth.service.AuthService;
import com.library.common.constant.LibraryConstants;
import com.library.common.context.UserContext;
import com.library.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器（W01-W03）。
 *
 * @author DTCoder
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * W01 用户登录。
     */
    @PostMapping("/login")
    public Result<LoginResult> login(@Valid @RequestBody LoginRequest req) {
        LoginResult result = authService.login(req);
        return Result.success(result);
    }

    /**
     * W02 获取当前用户信息。
     */
    @GetMapping("/current")
    public Result<CurrentUserVO> current(HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        SysUserDO user = authService.getById(userId);
        CurrentUserVO vo = new CurrentUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        return Result.success(vo);
    }

    /**
     * W03 退出登录。
     * <p>
     * JWT 为无状态 Token，退出仅由前端清除本地 Token；
     * 后端返回成功即可（如需主动失效需引入 Redis 黑名单，本期从简）。
     * </p>
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}

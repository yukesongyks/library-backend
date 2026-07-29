package com.library.auth.service;

import com.library.auth.dto.LoginRequest;
import com.library.auth.dto.LoginResult;
import com.library.auth.entity.SysUserDO;

/**
 * 认证服务接口。
 *
 * @author DTCoder
 */
public interface AuthService {

    /**
     * 用户登录，校验账号密码后签发 JWT Token。
     *
     * @param req 登录请求
     * @return 登录结果（Token + 角色）
     */
    LoginResult login(LoginRequest req);

    /**
     * 创建系统用户（新增读者时调用，内部事务）。
     *
     * @param username 用户名
     * @param password 明文密码
     * @param role     角色
     * @return 新建用户ID
     */
    Long createSysUser(String username, String password, String role);

    /**
     * 根据用户ID查询用户。
     *
     * @param userId 用户ID
     * @return 用户记录
     */
    SysUserDO getById(Long userId);

    /**
     * 校验用户名是否已存在。
     *
     * @param username 用户名
     * @return true=已存在
     */
    boolean existsByUsername(String username);
}

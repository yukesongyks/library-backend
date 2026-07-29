package com.library.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求。
 *
 * @author DTCoder
 */
@Data
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（明文，HTTPS传输） */
    @NotBlank(message = "密码不能为空")
    private String password;
}

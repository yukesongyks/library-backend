package com.library.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录结果。
 *
 * @author DTCoder
 */
@Data
@AllArgsConstructor
public class LoginResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** JWT Token */
    private String token;

    /** 角色 */
    private String role;
}

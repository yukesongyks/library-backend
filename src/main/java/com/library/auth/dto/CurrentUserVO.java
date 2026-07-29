package com.library.auth.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前用户信息。
 *
 * @author DTCoder
 */
@Data
public class CurrentUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 角色 */
    private String role;

    /** 状态 */
    private String status;
}

package com.library.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户 DO（sys_user 表）。
 *
 * @author DTCoder
 */
@Data
@TableName("sys_user")
public class SysUserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 系统自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名/登录账号 */
    private String username;

    /** 密码（BCrypt加密存储） */
    private String password;

    /** 角色：ADMIN/READER */
    private String role;

    /** 状态：ACTIVE/FROZEN */
    private String status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}

package com.library.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 读者 DO
 *
 * @author library
 */
@Data
public class ReaderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 读者姓名 */
    private String name;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}

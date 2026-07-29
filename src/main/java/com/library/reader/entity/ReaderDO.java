package com.library.reader.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 读者信息 DO（reader 表）。
 *
 * @author DTCoder
 */
@Data
@TableName("reader")
public class ReaderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 系统自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联sys_user.id */
    private Long userId;

    /** 读者编号/学号 */
    private String readerNo;

    /** 联系电话 */
    private String phone;

    /** 读者姓名 */
    private String name;

    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    @TableField("is_deleted")
    private Integer deleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}

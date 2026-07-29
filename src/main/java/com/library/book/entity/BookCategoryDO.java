package com.library.book.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书分类 DO（book_category 表）。
 *
 * @author DTCoder
 */
@Data
@TableName("book_category")
public class BookCategoryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 系统自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String name;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}

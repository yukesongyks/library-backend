package com.library.book.entity;

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
 * 图书信息 DO（book 表）。
 *
 * @author DTCoder
 */
@Data
@TableName("book")
public class BookDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 系统自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 书名 */
    private String title;

    /** 作者 */
    private String author;

    /** ISBN编号 */
    private String isbn;

    /** 分类ID */
    private Long categoryId;

    /** 当前库存数量 */
    private Integer stock;

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

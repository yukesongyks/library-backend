package com.library.borrow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 借阅记录 DO（borrow_record 表）。
 *
 * @author DTCoder
 */
@Data
@TableName("borrow_record")
public class BorrowRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 系统自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联book.id */
    private Long bookId;

    /** 读者关联sys_user.id */
    private Long userId;

    /** 借阅时间 */
    private LocalDateTime borrowDate;

    /** 应还时间（借阅+30天） */
    private LocalDateTime dueDate;

    /** 实际归还时间 */
    private LocalDateTime returnDate;

    /** 状态：BORROWING/RETURNED/OVERDUE */
    private String status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}

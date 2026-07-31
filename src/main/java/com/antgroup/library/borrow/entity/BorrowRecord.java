package com.antgroup.library.borrow.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅记录实体（对应表 borrow_record）。
 */
@Data
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookId;
    private Long readerId;
    private Date borrowTime;
    private Date dueTime;
    private Date returnTime;
    private String status;
    private Integer isOverdue;
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;
}

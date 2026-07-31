package com.antgroup.library.borrow.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅记录返回视图（W11）。含图书名称、读者姓名冗余字段。
 */
@Data
public class BorrowRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long readerId;
    private String readerName;
    private Date borrowTime;
    private Date dueTime;
    private Date returnTime;
    private String status;
    private Integer isOverdue;
}

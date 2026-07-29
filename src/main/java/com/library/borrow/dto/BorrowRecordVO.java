package com.library.borrow.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 借阅记录视图对象。
 *
 * @author DTCoder
 */
@Data
public class BorrowRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 图书ID */
    private Long bookId;

    /** 书名 */
    private String bookTitle;

    /** 借阅时间 */
    private LocalDateTime borrowDate;

    /** 应还时间 */
    private LocalDateTime dueDate;

    /** 实际归还时间 */
    private LocalDateTime returnDate;

    /** 状态 */
    private String status;
}

package com.library.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅记录 VO - 返回视图(含图书/读者冗余信息)
 *
 * @author library
 */
@Data
public class BorrowVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 图书ID */
    private Long bookId;

    /** 书名 */
    private String bookTitle;

    /** ISBN */
    private String bookIsbn;

    /** 读者ID */
    private Long readerId;

    /** 读者姓名 */
    private String readerName;

    /** 借阅时间 */
    private Date borrowTime;

    /** 应还时间 */
    private Date dueTime;

    /** 实际归还时间 */
    private Date returnTime;

    /** 状态: BORROWED-借阅中, RETURNED-已归还, OVERDUE-逾期 */
    private String status;

    /** 是否逾期 */
    private Boolean overdue;

    /** 逾期天数(已逾期时 >0) */
    private Integer overdueDays;

    /** 创建时间 */
    private Date createTime;
}

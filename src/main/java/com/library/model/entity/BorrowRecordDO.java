package com.library.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅记录 DO
 *
 * @author library
 */
@Data
public class BorrowRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 图书ID */
    private Long bookId;

    /** 读者ID */
    private Long readerId;

    /** 借阅时间 */
    private Date borrowTime;

    /** 应还时间 */
    private Date dueTime;

    /** 实际归还时间 */
    private Date returnTime;

    /** 状态: BORROWED-借阅中, RETURNED-已归还, OVERDUE-逾期 */
    private String status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    // ===== 联表查询冗余字段(非数据库列, 仅供 JOIN 映射) =====

    /** 书名(联表查询) */
    private String bookTitle;

    /** ISBN(联表查询) */
    private String bookIsbn;

    /** 读者姓名(联表查询) */
    private String readerName;

    /** 借阅次数(统计热门图书用) */
    private Integer borrowCount;
}

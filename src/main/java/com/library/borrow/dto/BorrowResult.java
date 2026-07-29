package com.library.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 借阅结果。
 *
 * @author DTCoder
 */
@Data
@AllArgsConstructor
public class BorrowResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 借阅记录ID */
    private Long recordId;

    /** 应还日期 */
    private LocalDateTime dueDate;
}

package com.library.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 归还结果。
 *
 * @author DTCoder
 */
@Data
@AllArgsConstructor
public class ReturnResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否逾期 */
    private Boolean isOverdue;

    /** 逾期天数（未逾期为0） */
    private Integer overdueDays;
}

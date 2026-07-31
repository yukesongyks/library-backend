package com.antgroup.library.borrow.dto;

import com.antgroup.library.common.response.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 借阅记录分页查询请求（W11）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BorrowRecordQueryRequest extends PageQuery {

    private Long readerId;
    private Long bookId;
    private String status;
}

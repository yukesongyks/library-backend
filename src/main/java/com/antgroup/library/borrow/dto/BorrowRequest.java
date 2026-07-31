package com.antgroup.library.borrow.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 借书请求（W12）。
 */
@Data
public class BorrowRequest {

    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    @NotNull(message = "读者ID不能为空")
    private Long readerId;
}

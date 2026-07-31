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

    /**
     * 幂等键（可选）。客户端生成的唯一请求标识，用于防止重复提交。
     * 未传时按 readerId+bookId 短窗口防重策略兜底。
     */
    private String requestId;
}

package com.library.borrow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 借阅请求。
 *
 * @author DTCoder
 */
@Data
public class BorrowRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 图书ID */
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
}

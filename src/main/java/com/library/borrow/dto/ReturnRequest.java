package com.library.borrow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 归还请求。
 *
 * @author DTCoder
 */
@Data
public class ReturnRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 借阅记录ID */
    @NotNull(message = "借阅记录ID不能为空")
    private Long recordId;
}

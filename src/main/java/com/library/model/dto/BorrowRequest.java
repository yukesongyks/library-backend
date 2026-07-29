package com.library.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 借阅 Request - 请求参数
 *
 * @author library
 */
@Data
public class BorrowRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 图书ID */
    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    /** 读者ID */
    @NotNull(message = "读者ID不能为空")
    private Long readerId;

    /** 借阅天数(可选, 默认30天) */
    private Integer borrowDays;
}

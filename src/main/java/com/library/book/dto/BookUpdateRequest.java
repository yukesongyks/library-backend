package com.library.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改图书请求。
 *
 * @author DTCoder
 */
@Data
public class BookUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 书名 */
    private String title;

    /** 作者 */
    private String author;

    /** 分类ID */
    private Long categoryId;

    /** 库存 */
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
}

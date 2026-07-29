package com.library.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增图书请求。
 *
 * @author DTCoder
 */
@Data
public class BookCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 书名 */
    @NotBlank(message = "书名不能为空")
    private String title;

    /** 作者 */
    @NotBlank(message = "作者不能为空")
    private String author;

    /** ISBN */
    @NotBlank(message = "ISBN不能为空")
    private String isbn;

    /** 分类ID */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /** 初始库存 */
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
}

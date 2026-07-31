package com.antgroup.library.book.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * 新增图书请求（W03）。
 */
@Data
public class BookCreateRequest {

    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名长度不能超过200")
    private String title;

    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过100")
    private String author;

    @NotBlank(message = "ISBN不能为空")
    @Size(max = 20, message = "ISBN长度不能超过20")
    private String isbn;

    @Size(max = 100, message = "出版社长度不能超过100")
    private String publisher;

    @Size(max = 50, message = "分类长度不能超过50")
    private String category;

    @NotNull(message = "初始库存不能为空")
    @PositiveOrZero(message = "初始库存不能为负数")
    private Integer stock;

    @Size(max = 500, message = "图书简介长度不能超过500")
    private String description;
}

package com.antgroup.library.book.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 修改图书请求（W04）。stock/totalStock 不通过此接口修改。
 */
@Data
public class BookUpdateRequest {

    @Size(max = 200, message = "书名长度不能超过200")
    private String title;

    @Size(max = 100, message = "作者长度不能超过100")
    private String author;

    @Size(max = 20, message = "ISBN长度不能超过20")
    private String isbn;

    @Size(max = 100, message = "出版社长度不能超过100")
    private String publisher;

    @Size(max = 50, message = "分类长度不能超过50")
    private String category;

    @Size(max = 500, message = "图书简介长度不能超过500")
    private String description;
}

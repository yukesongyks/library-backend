package com.antgroup.library.book.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 图书返回视图（W01/W02）。
 */
@Data
public class BookVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String category;
    private Integer stock;
    private Integer totalStock;
    private String description;
}

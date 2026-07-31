package com.antgroup.library.book.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图书信息实体（对应表 book）。
 */
@Data
public class Book implements Serializable {

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
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;
}

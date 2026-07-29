package com.library.book.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书视图对象。
 *
 * @author DTCoder
 */
@Data
public class BookVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 图书ID */
    private Long id;

    /** 书名 */
    private String title;

    /** 作者 */
    private String author;

    /** ISBN */
    private String isbn;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称 */
    private String categoryName;

    /** 当前库存数量 */
    private Integer stock;
}

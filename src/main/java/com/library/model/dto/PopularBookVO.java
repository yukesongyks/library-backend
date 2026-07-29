package com.library.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 热门图书 VO
 *
 * @author library
 */
@Data
public class PopularBookVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 图书ID */
    private Long bookId;

    /** 书名 */
    private String bookTitle;

    /** ISBN */
    private String bookIsbn;

    /** 作者 */
    private String author;

    /** 分类 */
    private String category;

    /** 借阅次数 */
    private Integer borrowCount;
}

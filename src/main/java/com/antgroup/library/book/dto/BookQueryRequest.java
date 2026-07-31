package com.antgroup.library.book.dto;

import com.antgroup.library.common.response.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图书分页查询请求（W01）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BookQueryRequest extends PageQuery {

    private String title;
    private String author;
    private String isbn;
}

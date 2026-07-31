package com.library.controller.vo;

import lombok.Data;

/**
 * 图书分页查询参数
 *
 * @author library-team
 */
@Data
public class BookPageQuery {

    /**
     * 页码(从1开始)
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;
}

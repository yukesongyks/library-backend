package com.antgroup.library.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求基类（设计文档分页参数默认值约束）。
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private Integer pageNum;
    private Integer pageSize;

    public int normalizedPageNum() {
        if (pageNum == null || pageNum < 1) {
            return DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    public int normalizedPageSize() {
        if (pageSize == null || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            return DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    public int offset() {
        return (normalizedPageNum() - 1) * normalizedPageSize();
    }
}

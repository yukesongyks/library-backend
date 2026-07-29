package com.library.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页返回结果
 *
 * @author library
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码 */
    private int pageNum;

    /** 每页条数 */
    private int pageSize;

    /** 总记录数 */
    private long total;

    /** 总页数 */
    private int pages;

    /** 数据列表 */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(int pageNum, int pageSize, long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = pageSize > 0 ? (int) ((total + pageSize - 1) / pageSize) : 0;
        this.list = list == null ? Collections.emptyList() : list;
    }

    /**
     * 空结果
     */
    public static <T> PageResult<T> empty(int pageNum, int pageSize) {
        return new PageResult<>(pageNum, pageSize, 0, Collections.emptyList());
    }
}

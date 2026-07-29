package com.library.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结构。
 *
 * @param <T> 列表元素类型
 * @author DTCoder
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long total;

    /** 当前页列表 */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}

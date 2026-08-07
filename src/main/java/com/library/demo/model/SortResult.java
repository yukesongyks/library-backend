package com.library.demo.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 冒泡排序结果
 */
@Data
public class SortResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> sorted;
    private Long costMs;
    private Integer size;
}

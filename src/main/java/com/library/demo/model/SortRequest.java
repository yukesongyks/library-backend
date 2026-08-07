package com.library.demo.model;

import lombok.Data;

import java.util.List;

/**
 * 冒泡排序请求
 */
@Data
public class SortRequest {

    /** 待排序整型数组，长度1~1000 */
    private List<Integer> numbers;
}

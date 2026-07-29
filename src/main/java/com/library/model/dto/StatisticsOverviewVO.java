package com.library.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 统计概览 VO
 *
 * @author library
 */
@Data
public class StatisticsOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 图书总数 */
    private Long totalBooks;

    /** 在架库存总数 */
    private Long totalInStock;

    /** 借出总数 */
    private Long totalBorrowed;

    /** 读者总数 */
    private Long totalReaders;

    /** 当前借阅中数量 */
    private Long currentBorrowing;

    /** 逾期数量 */
    private Long overdueCount;
}

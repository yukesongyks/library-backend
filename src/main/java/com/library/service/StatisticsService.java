package com.library.service;

import com.library.common.PageResult;
import com.library.model.dto.BorrowVO;
import com.library.model.dto.PopularBookVO;
import com.library.model.dto.StatisticsOverviewVO;

import java.util.List;

/**
 * 统计 Service
 *
 * @author library
 */
public interface StatisticsService {

    /**
     * 统计概览(在架/借出数量、读者数、当前借阅数、逾期数)
     */
    StatisticsOverviewVO getOverview();

    /**
     * 热门图书(按借阅次数排序)
     *
     * @param limit 返回条数
     */
    List<PopularBookVO> getPopularBooks(int limit);

    /**
     * 逾期列表(分页)
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     */
    PageResult<BorrowVO> getOverdueList(int pageNum, int pageSize);
}

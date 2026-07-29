package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.model.dto.BorrowVO;
import com.library.model.dto.PopularBookVO;
import com.library.model.dto.StatisticsOverviewVO;
import com.library.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计 Controller
 *
 * @author library
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 统计概览(在架/借出数量、读者数、当前借阅数、逾期数)
     */
    @GetMapping("/overview")
    public Result<StatisticsOverviewVO> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    /**
     * 热门图书(按借阅次数排序)
     */
    @GetMapping("/popular")
    public Result<List<PopularBookVO>> getPopularBooks(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return Result.success(statisticsService.getPopularBooks(limit));
    }

    /**
     * 逾期列表(分页)
     */
    @GetMapping("/overdue")
    public Result<PageResult<BorrowVO>> getOverdueList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResult<BorrowVO> page = statisticsService.getOverdueList(pageNum, pageSize);
        return Result.success(page);
    }
}

package com.library.track.controller;

import com.library.common.exception.BizException;
import com.library.common.model.ApiResponse;
import com.library.track.model.StatisticsQuery;
import com.library.track.model.StatisticsVO;
import com.library.track.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调用统计查询接口（W05）
 */
@RestController
@RequestMapping("/api/demo")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * W05 调用统计查询
     */
    @GetMapping("/statistics")
    public ApiResponse<StatisticsVO> statistics(
            @RequestParam(value = "dimension", required = false) String dimension,
            @RequestParam(value = "bizType", required = false) String bizType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

        StatisticsQuery query = new StatisticsQuery();
        query.setDimension(dimension);
        query.setBizType(bizType);
        query.setStartDate(startDate);
        query.setEndDate(endDate);

        try {
            StatisticsVO data = statisticsService.query(query);
            return ApiResponse.success(data);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException("TRACK_003", "统计查询异常: " + e.getMessage());
        }
    }
}

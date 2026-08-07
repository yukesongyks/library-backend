package com.antdigital.library.track.controller;

import com.antdigital.library.common.response.ApiResponse;
import com.antdigital.library.track.model.vo.TrackStatisticsVO;
import com.antdigital.library.track.service.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 埋点统计 Controller。
 *
 * @author library-backend
 */
@RestController
@RequestMapping("/api/track")
public class TrackController {

    private static final Logger logger = LoggerFactory.getLogger(TrackController.class);

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    /**
     * 查询调用统计数据。
     *
     * @param dimension 维度（user_type / user_level / user_department / user_id）
     * @param chartType 图表类型（pie / bar / line）
     * @return 统计结果
     */
    @GetMapping("/statistics")
    public ApiResponse<TrackStatisticsVO> getStatistics(
            @RequestParam String dimension,
            @RequestParam String chartType) {
        logger.info("收到统计查询请求, dimension: {}, chartType: {}", dimension, chartType);
        return ApiResponse.success(trackService.getStatistics(dimension, chartType));
    }
}

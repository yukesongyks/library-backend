package com.antdigital.library.track.service.impl;

import com.antdigital.library.common.exception.ErrorCodeEnum;
import com.antdigital.library.common.exception.ServiceException;
import com.antdigital.library.track.model.entity.TrackRecordDO;
import com.antdigital.library.track.model.vo.TrackStatisticsVO;
import com.antdigital.library.track.repository.TrackRecordRepository;
import com.antdigital.library.track.service.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 埋点统计服务实现。
 *
 * @author library-backend
 */
@Service
public class TrackServiceImpl implements TrackService {

    private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);

    /** 允许的统计维度白名单，防止 SQL 注入 */
    private static final Set<String> ALLOWED_DIMENSIONS = Set.of(
            "user_type", "user_level", "user_department", "user_id"
    );

    /** 允许的图表类型 */
    private static final Set<String> ALLOWED_CHART_TYPES = Set.of(
            "pie", "bar", "line"
    );

    private final TrackRecordRepository trackRecordRepository;

    public TrackServiceImpl(TrackRecordRepository trackRecordRepository) {
        this.trackRecordRepository = trackRecordRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTrackRecord(String apiPath, String userId, String userName,
                                String userType, String userLevel, String userDepartment) {
        TrackRecordDO record = new TrackRecordDO();
        record.setApiPath(apiPath);
        record.setUserId(userId);
        record.setUserName(userName);
        record.setUserType(userType);
        record.setUserLevel(userLevel);
        record.setUserDepartment(userDepartment);
        record.setCallTime(LocalDateTime.now());

        trackRecordRepository.save(record);
        logger.info("埋点记录已保存, apiPath: {}, userId: {}", apiPath, userId);
    }

    @Override
    public TrackStatisticsVO getStatistics(String dimension, String chartType) {
        // 校验维度参数
        if (dimension == null || !ALLOWED_DIMENSIONS.contains(dimension)) {
            logger.warn("不支持的统计维度: {}", dimension);
            throw new ServiceException(ErrorCodeEnum.PARAM_INVALID,
                    "不支持的统计维度: " + dimension,
                    "请选择有效的统计维度");
        }
        // 校验图表类型
        if (chartType == null || !ALLOWED_CHART_TYPES.contains(chartType)) {
            logger.warn("不支持的图表类型: {}", chartType);
            throw new ServiceException(ErrorCodeEnum.PARAM_INVALID,
                    "不支持的图表类型: " + chartType,
                    "请选择有效的图表类型");
        }

        TrackStatisticsVO vo = new TrackStatisticsVO();
        vo.setDimension(dimension);
        vo.setChartType(chartType);

        if ("line".equals(chartType)) {
            // 折线图：按日期+维度聚合
            List<Object[]> rows = trackRecordRepository.countByDateAndDimension(dimension);
            List<TrackStatisticsVO.DateDimensionCount> timeSeriesData = new ArrayList<>(rows.size());
            for (Object[] row : rows) {
                String date = row[0] != null ? row[0].toString() : "";
                String dimensionValue = row[1] != null ? row[1].toString() : "unknown";
                Long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;
                timeSeriesData.add(new TrackStatisticsVO.DateDimensionCount(date, dimensionValue, count));
            }
            vo.setTimeSeriesData(timeSeriesData);
            vo.setCategoryData(new ArrayList<>());
        } else {
            // 饼图/柱状图：按维度聚合
            List<Object[]> rows = trackRecordRepository.countByDimension(dimension);
            List<TrackStatisticsVO.DimensionCount> categoryData = new ArrayList<>(rows.size());
            for (Object[] row : rows) {
                String dimensionValue = row[0] != null ? row[0].toString() : "unknown";
                Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                categoryData.add(new TrackStatisticsVO.DimensionCount(dimensionValue, count));
            }
            vo.setCategoryData(categoryData);
            vo.setTimeSeriesData(new ArrayList<>());
        }

        logger.info("统计查询完成, dimension: {}, chartType: {}", dimension, chartType);
        return vo;
    }
}

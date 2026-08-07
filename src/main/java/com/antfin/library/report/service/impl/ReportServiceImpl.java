package com.antfin.library.report.service.impl;

import com.antfin.library.common.enums.DimensionEnum;
import com.antfin.library.common.exception.BusinessException;
import com.antfin.library.report.model.request.ReportRequest;
import com.antfin.library.report.model.vo.AlgoCallStatsVO;
import com.antfin.library.report.service.ReportService;
import com.antfin.library.tracking.dao.mapper.AlgoCallLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现
 */
@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private AlgoCallLogMapper algoCallLogMapper;

    @Override
    public AlgoCallStatsVO queryCallStats(ReportRequest request) {
        // 校验维度
        if (!DimensionEnum.isValid(request.getDimension())) {
            throw new BusinessException("PARAM_ERROR", "无效的聚合维度: " + request.getDimension() +
                    "，可选值: USER_TYPE / USER_LEVEL / DEPARTMENT");
        }

        // 解析日期
        Date startDate = parseStartDate(request.getStartDate());
        Date endDate = parseEndDate(request.getEndDate());
        if (startDate.after(endDate)) {
            throw new BusinessException("PARAM_ERROR", "开始日期不能晚于结束日期");
        }

        String algorithmType = request.getAlgorithmType();
        String dimension = request.getDimension();

        AlgoCallStatsVO vo = new AlgoCallStatsVO();

        // 1. 按日趋势
        List<Map<String, Object>> trendRows = algoCallLogMapper.selectDailyTrend(algorithmType, startDate, endDate);
        List<AlgoCallStatsVO.TrendItem> trendList = new ArrayList<>();
        for (Map<String, Object> row : trendRows) {
            String date = String.valueOf(row.get("date"));
            Long count = toLong(row.get("count"));
            trendList.add(new AlgoCallStatsVO.TrendItem(date, count));
        }
        vo.setTrendList(trendList);

        // 2. 维度占比（饼图）
        List<Map<String, Object>> ratioRows = algoCallLogMapper.selectDimensionRatio(algorithmType, dimension, startDate, endDate);
        List<AlgoCallStatsVO.DimensionItem> ratioList = new ArrayList<>();
        for (Map<String, Object> row : ratioRows) {
            String name = String.valueOf(row.get("name"));
            Long value = toLong(row.get("value"));
            ratioList.add(new AlgoCallStatsVO.DimensionItem(name, value));
        }
        vo.setDimensionRatioList(ratioList);

        // 3. 维度对比（柱状图）
        List<Map<String, Object>> comparisonRows = algoCallLogMapper.selectDimensionComparison(dimension, startDate, endDate);
        List<AlgoCallStatsVO.DimensionItem> comparisonList = new ArrayList<>();
        for (Map<String, Object> row : comparisonRows) {
            String name = String.valueOf(row.get("name"));
            Long value = toLong(row.get("value"));
            String algoType = String.valueOf(row.get("algorithmType"));
            comparisonList.add(new AlgoCallStatsVO.DimensionItem(name, value, algoType));
        }
        vo.setDimensionComparisonList(comparisonList);

        // 4. 总调用次数
        Long totalCall = algoCallLogMapper.countTotalCall(algorithmType, startDate, endDate);
        vo.setTotalCallCount(totalCall != null ? totalCall : 0L);

        // 5. 去重用户数
        Long distinctUser = algoCallLogMapper.countDistinctUser(algorithmType, startDate, endDate);
        vo.setDistinctUserCount(distinctUser != null ? distinctUser : 0L);

        log.info("报表查询完成: dimension={}, algorithmType={}, totalCallCount={}", dimension, algorithmType, vo.getTotalCallCount());

        return vo;
    }

    /**
     * 解析开始日期（当天 00:00:00）
     */
    private Date parseStartDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
            return sdf.parse(dateStr + " 00:00:00");
        } catch (ParseException e) {
            throw new BusinessException("PARAM_ERROR", "日期格式错误，应为 yyyy-MM-dd: " + dateStr);
        }
    }

    /**
     * 解析结束日期（当天 23:59:59）
     */
    private Date parseEndDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
            return sdf.parse(dateStr + " 23:59:59");
        } catch (ParseException e) {
            throw new BusinessException("PARAM_ERROR", "日期格式错误，应为 yyyy-MM-dd: " + dateStr);
        }
    }

    /**
     * 安全转 Long
     */
    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
}

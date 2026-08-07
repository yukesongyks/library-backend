package com.library.track.service;

import com.library.common.enums.StatisticsDimensionEnum;
import com.library.common.exception.BizException;
import com.library.track.mapper.CallRecordMapper;
import com.library.track.model.StatisticsQuery;
import com.library.track.model.StatisticsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计查询服务（S06）
 * 降级兜底：查询异常返回空数据（异常兜底方案 6.1.3）
 */
@Service
public class StatisticsService {

    private static final Logger log = LoggerFactory.getLogger(StatisticsService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CallRecordMapper callRecordMapper;

    @Value("${demo.statistics.enabled:true}")
    private boolean statisticsEnabled;

    /**
     * 统计查询
     */
    public StatisticsVO query(StatisticsQuery query) {
        // R01: dimension 非空
        if (query.getDimension() == null || query.getDimension().trim().isEmpty()) {
            throw new BizException("TRACK_001", "dimension不能为空");
        }
        // R02: 维度合法性
        StatisticsDimensionEnum dimEnum = StatisticsDimensionEnum.fromCode(query.getDimension().trim());
        if (dimEnum == null) {
            throw new BizException("TRACK_002", "不支持的统计维度: " + query.getDimension());
        }
        if (!statisticsEnabled) {
            return StatisticsVO.empty(dimEnum.getCode());
        }

        // 默认近7天
        String startDate = query.getStartDate();
        String endDate = query.getEndDate();
        if (startDate == null || startDate.trim().isEmpty()) {
            startDate = LocalDate.now().minusDays(6).format(DATE_FMT);
        }
        if (endDate == null || endDate.trim().isEmpty()) {
            endDate = LocalDate.now().format(DATE_FMT);
        }

        String dimensionColumn = toDimensionColumn(dimEnum);

        try {
            StatisticsVO vo = new StatisticsVO();
            vo.setDimension(dimEnum.getCode());

            // 总数
            Long total = callRecordMapper.countTotal(query.getBizType(), startDate, endDate);
            vo.setTotal(total == null ? 0L : total);

            // 饼图 + 柱状图数据
            List<Map<String, Object>> dimAgg = callRecordMapper.aggregateByDimension(
                    dimensionColumn, query.getBizType(), startDate, endDate);
            vo.setPie(buildPie(dimAgg));
            vo.setBar(buildBar(dimAgg));

            // 折线图数据
            List<Map<String, Object>> dateAgg = callRecordMapper.aggregateByDimensionAndDate(
                    dimensionColumn, query.getBizType(), startDate, endDate);
            vo.setLine(buildLine(dateAgg, startDate, endDate));

            return vo;
        } catch (Exception e) {
            log.error("统计查询异常(降级返回空数据): dimension={}, error={}", query.getDimension(), e.getMessage(), e);
            return StatisticsVO.empty(dimEnum.getCode());
        }
    }

    private String toDimensionColumn(StatisticsDimensionEnum dim) {
        switch (dim) {
            case CALLER_TYPE:
                return "caller_type";
            case CALLER_LEVEL:
                return "caller_level";
            case CALLER_DEPT:
                return "caller_dept";
            default:
                return "caller_type";
        }
    }

    private List<StatisticsVO.PieItem> buildPie(List<Map<String, Object>> agg) {
        List<StatisticsVO.PieItem> items = new ArrayList<>();
        if (agg == null) {
            return items;
        }
        for (Map<String, Object> row : agg) {
            StatisticsVO.PieItem item = new StatisticsVO.PieItem();
            item.setName(String.valueOf(row.get("dim_value")));
            item.setValue(toLong(row.get("cnt")));
            items.add(item);
        }
        return items;
    }

    private StatisticsVO.BarData buildBar(List<Map<String, Object>> agg) {
        StatisticsVO.BarData bar = new StatisticsVO.BarData();
        List<String> categories = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        if (agg != null) {
            for (Map<String, Object> row : agg) {
                categories.add(String.valueOf(row.get("dim_value")));
                data.add(toLong(row.get("cnt")));
            }
        }
        bar.setCategories(categories);
        List<StatisticsVO.BarSeries> seriesList = new ArrayList<>();
        StatisticsVO.BarSeries series = new StatisticsVO.BarSeries();
        series.setName("调用次数");
        series.setData(data);
        seriesList.add(series);
        bar.setSeries(seriesList);
        return bar;
    }

    private StatisticsVO.LineData buildLine(List<Map<String, Object>> agg, String startDate, String endDate) {
        StatisticsVO.LineData line = new StatisticsVO.LineData();

        // 生成日期序列
        List<String> dates = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate, DATE_FMT);
        LocalDate end = LocalDate.parse(endDate, DATE_FMT);
        LocalDate cur = start;
        while (!cur.isAfter(end)) {
            dates.add(cur.format(DATE_FMT));
            cur = cur.plusDays(1);
        }
        line.setDates(dates);

        // 按维度值分组
        Map<String, Map<String, Long>> dimDateCount = new LinkedHashMap<>();
        if (agg != null) {
            for (Map<String, Object> row : agg) {
                String dimValue = String.valueOf(row.get("dim_value"));
                String date = String.valueOf(row.get("call_date"));
                Long cnt = toLong(row.get("cnt"));
                dimDateCount.computeIfAbsent(dimValue, k -> new LinkedHashMap<>()).put(date, cnt);
            }
        }

        List<StatisticsVO.LineSeries> seriesList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Long>> entry : dimDateCount.entrySet()) {
            StatisticsVO.LineSeries series = new StatisticsVO.LineSeries();
            series.setName(entry.getKey());
            List<Long> data = new ArrayList<>();
            for (String date : dates) {
                data.add(entry.getValue().getOrDefault(date, 0L));
            }
            series.setData(data);
            seriesList.add(series);
        }
        line.setSeries(seriesList);
        return line;
    }

    private Long toLong(Object obj) {
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}

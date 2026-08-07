package com.library.track.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统计查询结果（W05 出参）
 */
@Data
public class StatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dimension;
    private Long total;

    /** 饼图数据 */
    private List<PieItem> pie;

    /** 柱状图数据 */
    private BarData bar;

    /** 折线图数据 */
    private LineData line;

    @Data
    public static class PieItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Long value;
    }

    @Data
    public static class BarData implements Serializable {
        private static final long serialVersionUID = 1L;
        private List<String> categories;
        private List<BarSeries> series;
    }

    @Data
    public static class BarSeries implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private List<Long> data;
    }

    @Data
    public static class LineData implements Serializable {
        private static final long serialVersionUID = 1L;
        private List<String> dates;
        private List<LineSeries> series;
    }

    @Data
    public static class LineSeries implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private List<Long> data;
    }

    /**
     * 空数据兜底（异常兜底方案 6.1.3）
     */
    public static StatisticsVO empty(String dimension) {
        StatisticsVO vo = new StatisticsVO();
        vo.setDimension(dimension);
        vo.setTotal(0L);
        vo.setPie(new java.util.ArrayList<>());
        BarData bar = new BarData();
        bar.setCategories(new java.util.ArrayList<>());
        bar.setSeries(new java.util.ArrayList<>());
        vo.setBar(bar);
        LineData line = new LineData();
        line.setDates(new java.util.ArrayList<>());
        line.setSeries(new java.util.ArrayList<>());
        vo.setLine(line);
        return vo;
    }
}

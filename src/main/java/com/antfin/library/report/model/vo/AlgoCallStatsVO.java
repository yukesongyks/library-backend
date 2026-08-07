package com.antfin.library.report.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 算法调用统计报表 VO
 */
public class AlgoCallStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 按日趋势数据（折线图）
     */
    private List<TrendItem> trendList;

    /**
     * 维度占比数据（饼图）
     */
    private List<DimensionItem> dimensionRatioList;

    /**
     * 维度对比数据（柱状图）
     */
    private List<DimensionItem> dimensionComparisonList;

    /**
     * 总调用次数
     */
    private Long totalCallCount;

    /**
     * 去重用户数
     */
    private Long distinctUserCount;

    public List<TrendItem> getTrendList() {
        return trendList;
    }

    public void setTrendList(List<TrendItem> trendList) {
        this.trendList = trendList;
    }

    public List<DimensionItem> getDimensionRatioList() {
        return dimensionRatioList;
    }

    public void setDimensionRatioList(List<DimensionItem> dimensionRatioList) {
        this.dimensionRatioList = dimensionRatioList;
    }

    public List<DimensionItem> getDimensionComparisonList() {
        return dimensionComparisonList;
    }

    public void setDimensionComparisonList(List<DimensionItem> dimensionComparisonList) {
        this.dimensionComparisonList = dimensionComparisonList;
    }

    public Long getTotalCallCount() {
        return totalCallCount;
    }

    public void setTotalCallCount(Long totalCallCount) {
        this.totalCallCount = totalCallCount;
    }

    public Long getDistinctUserCount() {
        return distinctUserCount;
    }

    public void setDistinctUserCount(Long distinctUserCount) {
        this.distinctUserCount = distinctUserCount;
    }

    /**
     * 趋势项
     */
    public static class TrendItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String date;
        private Long count;

        public TrendItem() {
        }

        public TrendItem(String date, Long count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    /**
     * 维度项
     */
    public static class DimensionItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Long value;
        private String algorithmType;

        public DimensionItem() {
        }

        public DimensionItem(String name, Long value) {
            this.name = name;
            this.value = value;
        }

        public DimensionItem(String name, Long value, String algorithmType) {
            this.name = name;
            this.value = value;
            this.algorithmType = algorithmType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }

        public String getAlgorithmType() {
            return algorithmType;
        }

        public void setAlgorithmType(String algorithmType) {
            this.algorithmType = algorithmType;
        }
    }
}

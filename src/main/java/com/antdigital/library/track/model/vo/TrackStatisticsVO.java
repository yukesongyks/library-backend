package com.antdigital.library.track.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 埋点统计结果视图对象。
 *
 * <p>支持折线图、饼图、柱状图三种展示形式。</p>
 *
 * @author library-backend
 */
public class TrackStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计维度（user_type / user_level / user_department / user_id） */
    private String dimension;

    /** 图表类型（pie / bar / line） */
    private String chartType;

    /** 饼图/柱状图数据：维度值 -> 调用次数 */
    private List<DimensionCount> categoryData;

    /** 折线图数据：按日期分组的维度值 -> 调用次数 */
    private List<DateDimensionCount> timeSeriesData;

    public TrackStatisticsVO() {
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public List<DimensionCount> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(List<DimensionCount> categoryData) {
        this.categoryData = categoryData;
    }

    public List<DateDimensionCount> getTimeSeriesData() {
        return timeSeriesData;
    }

    public void setTimeSeriesData(List<DateDimensionCount> timeSeriesData) {
        this.timeSeriesData = timeSeriesData;
    }

    /**
     * 维度聚合项（饼图/柱状图用）。
     */
    public static class DimensionCount implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 维度值（如人员类型名称） */
        private String dimensionValue;

        /** 调用次数 */
        private Long callCount;

        public DimensionCount() {
        }

        public DimensionCount(String dimensionValue, Long callCount) {
            this.dimensionValue = dimensionValue;
            this.callCount = callCount;
        }

        public String getDimensionValue() {
            return dimensionValue;
        }

        public void setDimensionValue(String dimensionValue) {
            this.dimensionValue = dimensionValue;
        }

        public Long getCallCount() {
            return callCount;
        }

        public void setCallCount(Long callCount) {
            this.callCount = callCount;
        }
    }

    /**
     * 日期维度聚合项（折线图用）。
     */
    public static class DateDimensionCount implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 日期（yyyy-MM-dd） */
        private String date;

        /** 维度值 */
        private String dimensionValue;

        /** 调用次数 */
        private Long callCount;

        public DateDimensionCount() {
        }

        public DateDimensionCount(String date, String dimensionValue, Long callCount) {
            this.date = date;
            this.dimensionValue = dimensionValue;
            this.callCount = callCount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDimensionValue() {
            return dimensionValue;
        }

        public void setDimensionValue(String dimensionValue) {
            this.dimensionValue = dimensionValue;
        }

        public Long getCallCount() {
            return callCount;
        }

        public void setCallCount(Long callCount) {
            this.callCount = callCount;
        }
    }

    @Override
    public String toString() {
        return "TrackStatisticsVO{"
                + "dimension='" + dimension + '\''
                + ", chartType='" + chartType + '\''
                + ", categoryData=" + categoryData
                + ", timeSeriesData=" + timeSeriesData
                + '}';
    }
}

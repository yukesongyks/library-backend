package com.antfin.library.report.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 算法调用统计报表请求
 */
public class ReportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 算法维度过滤，不传则全部
     */
    private String algorithmType;

    /**
     * 聚合维度：USER_TYPE / USER_LEVEL / DEPARTMENT
     */
    @NotBlank(message = "聚合维度不能为空")
    private String dimension;

    /**
     * 查询开始日期，格式 yyyy-MM-dd
     */
    @NotNull(message = "开始日期不能为空")
    private String startDate;

    /**
     * 查询结束日期，格式 yyyy-MM-dd
     */
    @NotNull(message = "结束日期不能为空")
    private String endDate;

    public String getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}

package com.library.track.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统计查询入参
 */
@Data
public class StatisticsQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计维度 */
    private String dimension;
    /** 业务类型过滤（可选） */
    private String bizType;
    /** 开始日期 yyyy-MM-dd */
    private String startDate;
    /** 结束日期 yyyy-MM-dd */
    private String endDate;
}

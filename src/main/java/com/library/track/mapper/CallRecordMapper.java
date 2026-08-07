package com.library.track.mapper;

import com.library.track.model.CallRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 调用埋点记录 Mapper
 */
@Mapper
public interface CallRecordMapper {

    /**
     * 插入埋点记录
     */
    int insert(CallRecord record);

    /**
     * 按维度聚合统计（饼图/柱状图）
     */
    List<Map<String, Object>> aggregateByDimension(
            @Param("dimensionColumn") String dimensionColumn,
            @Param("bizType") String bizType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /**
     * 按维度+日期聚合统计（折线图）
     */
    List<Map<String, Object>> aggregateByDimensionAndDate(
            @Param("dimensionColumn") String dimensionColumn,
            @Param("bizType") String bizType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    /**
     * 统计总数
     */
    Long countTotal(
            @Param("bizType") String bizType,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}

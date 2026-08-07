package com.antfin.library.tracking.dao.mapper;

import com.antfin.library.tracking.dao.entity.AlgoCallLogEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 算法调用埋点 Mapper
 */
public interface AlgoCallLogMapper {

    /**
     * 插入埋点记录
     */
    int insert(AlgoCallLogEntity entity);

    /**
     * 按日趋势聚合
     */
    List<Map<String, Object>> selectDailyTrend(@Param("algorithmType") String algorithmType,
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate);

    /**
     * 按维度占比聚合
     */
    List<Map<String, Object>> selectDimensionRatio(@Param("algorithmType") String algorithmType,
                                                    @Param("dimension") String dimension,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate);

    /**
     * 按维度对比聚合（各算法在各维度值上的调用次数）
     */
    List<Map<String, Object>> selectDimensionComparison(@Param("dimension") String dimension,
                                                         @Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate);

    /**
     * 总调用次数
     */
    Long countTotalCall(@Param("algorithmType") String algorithmType,
                         @Param("startDate") Date startDate,
                         @Param("endDate") Date endDate);

    /**
     * 去重用户数
     */
    Long countDistinctUser(@Param("algorithmType") String algorithmType,
                           @Param("startDate") Date startDate,
                           @Param("endDate") Date endDate);
}

package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.ApiMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface ApiMetricsMapper extends BaseMapper<ApiMetrics> {

    @Select("SELECT ${dimension} AS label, COUNT(*) AS count, " +
            "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM api_metrics WHERE 1=1 " +
            "${whereCondition}), 1) AS percentage " +
            "FROM api_metrics WHERE 1=1 ${whereCondition} " +
            "GROUP BY ${dimension} ORDER BY count DESC")
    List<Map<String, Object>> selectStatsByDimension(
            @Param("dimension") String dimension,
            @Param("whereCondition") String whereCondition);

    @Select("SELECT DATE(call_time) AS date, COUNT(*) AS count " +
            "FROM api_metrics WHERE 1=1 ${whereCondition} " +
            "GROUP BY DATE(call_time) ORDER BY date")
    List<Map<String, Object>> selectTrend(@Param("whereCondition") String whereCondition);

    @Select("SELECT COUNT(*) FROM api_metrics WHERE 1=1 ${whereCondition}")
    Long selectTotalCount(@Param("whereCondition") String whereCondition);
}
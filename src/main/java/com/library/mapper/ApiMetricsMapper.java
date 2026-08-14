package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.ApiMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface ApiMetricsMapper extends BaseMapper<ApiMetrics> {

    /** 允许的维度列名白名单，防御 SQL 注入 */
    Set<String> ALLOWED_DIMENSIONS = Set.of("caller_type", "caller_level", "caller_dept");

    @Select("<script>"
            + "SELECT ${dimension} AS label, COUNT(*) AS count, "
            + "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM api_metrics WHERE 1=1 "
            + "<if test='startDate != null'> AND call_time &gt;= #{startDate}</if>"
            + "<if test='endDate != null'> AND call_time &lt;= #{endDate}</if>"
            + "<if test='apiPath != null'> AND api_path = #{apiPath}</if>"
            + "), 1) AS percentage "
            + "FROM api_metrics WHERE 1=1 "
            + "<if test='startDate != null'> AND call_time &gt;= #{startDate}</if>"
            + "<if test='endDate != null'> AND call_time &lt;= #{endDate}</if>"
            + "<if test='apiPath != null'> AND api_path = #{apiPath}</if>"
            + " GROUP BY ${dimension} ORDER BY count DESC"
            + "</script>")
    List<Map<String, Object>> selectStatsByDimension(
            @Param("dimension") String dimension,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("apiPath") String apiPath);

    @Select("<script>"
            + "SELECT DATE(call_time) AS date, COUNT(*) AS count "
            + "FROM api_metrics WHERE 1=1 "
            + "<if test='startDate != null'> AND call_time &gt;= #{startDate}</if>"
            + "<if test='endDate != null'> AND call_time &lt;= #{endDate}</if>"
            + "<if test='apiPath != null'> AND api_path = #{apiPath}</if>"
            + " GROUP BY DATE(call_time) ORDER BY date"
            + "</script>")
    List<Map<String, Object>> selectTrend(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("apiPath") String apiPath);

    @Select("<script>"
            + "SELECT COUNT(*) FROM api_metrics WHERE 1=1 "
            + "<if test='startDate != null'> AND call_time &gt;= #{startDate}</if>"
            + "<if test='endDate != null'> AND call_time &lt;= #{endDate}</if>"
            + "<if test='apiPath != null'> AND api_path = #{apiPath}</if>"
            + "</script>")
    Long selectTotalCount(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("apiPath") String apiPath);
}
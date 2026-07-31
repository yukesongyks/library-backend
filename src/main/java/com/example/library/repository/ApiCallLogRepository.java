package com.example.library.repository;

import com.example.library.model.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API 调用日志 Repository：含按维度/按日聚合查询方法。
 * 对应 design.md 3.1 / tasks B3/E3。
 */
@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

    /**
     * 按任意维度字段聚合调用次数。dimensionColumn 为 ApiCallLog 列名。
     * 返回 [label, count] 二元组。
     */
    @Query("SELECT COALESCE(e.userType, 'unknown') AS label, COUNT(e) AS value " +
            "FROM ApiCallLog e WHERE e.calledAt >= :start AND e.calledAt < :end " +
            "GROUP BY COALESCE(e.userType, 'unknown') ORDER BY value DESC")
    List<Object[]> aggregateByUserType(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(e.userLevel, 'unknown') AS label, COUNT(e) AS value " +
            "FROM ApiCallLog e WHERE e.calledAt >= :start AND e.calledAt < :end " +
            "GROUP BY COALESCE(e.userLevel, 'unknown') ORDER BY value DESC")
    List<Object[]> aggregateByUserLevel(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(e.department, 'unknown') AS label, COUNT(e) AS value " +
            "FROM ApiCallLog e WHERE e.calledAt >= :start AND e.calledAt < :end " +
            "GROUP BY COALESCE(e.department, 'unknown') ORDER BY value DESC")
    List<Object[]> aggregateByDepartment(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT e.apiName AS label, COUNT(e) AS value " +
            "FROM ApiCallLog e WHERE e.calledAt >= :start AND e.calledAt < :end " +
            "GROUP BY e.apiName ORDER BY value DESC")
    List<Object[]> aggregateByApiName(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 按日聚合调用次数（折线图趋势）。
     * 返回 [date_string, count] 二元组。
     */
    @Query("SELECT FORMATDATETIME(e.calledAt, 'yyyy-MM-dd') AS label, COUNT(e) AS value " +
            "FROM ApiCallLog e WHERE e.calledAt >= :start AND e.calledAt < :end " +
            "GROUP BY FORMATDATETIME(e.calledAt, 'yyyy-MM-dd') " +
            "ORDER BY label ASC")
    List<Object[]> aggregateByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

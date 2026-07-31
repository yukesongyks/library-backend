package com.library.backend.repository;

import com.library.backend.model.CallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调用埋点 Repository。
 *
 * <p>提供两类聚合查询支撑分析接口（spec {@code tracking-analytics.md}）：
 * <ul>
 *   <li>bar / pie：按单一维度字段分组计数，返回 {@code [label, count]}。</li>
 *   <li>line：按天 + 维度分组计数，返回 {@code [date, label, count]}，前端聚合成
 *       {@code [{date, values:[{label,value}]}]}。</li>
 * </ul>
 * 维度字段在 CallLog 中为可空，null 值不参与聚合（与 spec 未关联用户场景一致）。</p>
 */
public interface CallLogRepository extends JpaRepository<CallLog, Long> {

    /**
     * 按 apiType 查询全部记录（导出接口使用）。
     */
    List<CallLog> findByApiTypeOrderByIdAsc(String apiType);

    /**
     * bar / pie 聚合：按维度字段分组计数。
     *
     * <p>因 dimension 是动态字段名，JPQL 无法直接参数化列名，故按 dimension 分支
     * 提供三个方法，由 {@code AnalyticsService} 依据入参选择调用。</p>
     */
    @Query("select c.personnelType as label, count(c) as value from CallLog c " +
           "where c.calledAt between :start and :end and c.personnelType is not null " +
           "group by c.personnelType order by c.personnelType")
    List<DimensionCount> aggregateByPersonnelType(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Query("select c.personnelLevel as label, count(c) as value from CallLog c " +
           "where c.calledAt between :start and :end and c.personnelLevel is not null " +
           "group by c.personnelLevel order by c.personnelLevel")
    List<DimensionCount> aggregateByPersonnelLevel(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    @Query("select c.department as label, count(c) as value from CallLog c " +
           "where c.calledAt between :start and :end and c.department is not null " +
           "group by c.department order by c.department")
    List<DimensionCount> aggregateByDepartment(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    /**
     * line 聚合：按天 + 维度分组计数。
     *
     * <p>{@code FUNCTION('DATE', c.calledAt)} 取日期部分，H2 支持。返回
     * {@code [date, label, count]}，前端按 date 分组聚合成折线图数据。</p>
     */
    @Query("select FUNCTION('DATE', c.calledAt) as date, c.department as label, count(c) as value " +
           "from CallLog c " +
           "where c.calledAt between :start and :end and c.department is not null " +
           "group by FUNCTION('DATE', c.calledAt), c.department " +
           "order by FUNCTION('DATE', c.calledAt), c.department")
    List<TrendCount> trendByDepartment(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    @Query("select FUNCTION('DATE', c.calledAt) as date, c.personnelType as label, count(c) as value " +
           "from CallLog c " +
           "where c.calledAt between :start and :end and c.personnelType is not null " +
           "group by FUNCTION('DATE', c.calledAt), c.personnelType " +
           "order by FUNCTION('DATE', c.calledAt), c.personnelType")
    List<TrendCount> trendByPersonnelType(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("select FUNCTION('DATE', c.calledAt) as date, c.personnelLevel as label, count(c) as value " +
           "from CallLog c " +
           "where c.calledAt between :start and :end and c.personnelLevel is not null " +
           "group by FUNCTION('DATE', c.calledAt), c.personnelLevel " +
           "order by FUNCTION('DATE', c.calledAt), c.personnelLevel")
    List<TrendCount> trendByPersonnelLevel(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    /**
     * 维度聚合投影（bar / pie）。
     */
    interface DimensionCount {
        String getLabel();
        Long getValue();
    }

    /**
     * 趋势聚合投影（line）。
     */
    interface TrendCount {
        java.time.LocalDate getDate();
        String getLabel();
        Long getValue();
    }
}

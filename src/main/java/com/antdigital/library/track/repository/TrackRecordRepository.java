package com.antdigital.library.track.repository;

import com.antdigital.library.track.model.entity.TrackRecordDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 埋点记录数据访问层。
 *
 * @author library-backend
 */
@Repository
public interface TrackRecordRepository extends JpaRepository<TrackRecordDO, Long> {

    /**
     * 按指定维度聚合统计调用次数。
     *
     * @param dimension 维度字段名（user_type / user_level / user_department / user_id）
     * @return 聚合结果列表
     */
    @Query(value = "SELECT t." + :dimension + " AS dimension_value, COUNT(*) AS call_count "
            + "FROM track_record t GROUP BY t." + :dimension
            + " ORDER BY call_count DESC",
            nativeQuery = true)
    List<Object[]> countByDimension(@Param("dimension") String dimension);

    /**
     * 按日期和维度聚合统计调用次数（用于折线图）。
     *
     * @param dimension 维度字段名
     * @return 聚合结果列表 [date, dimensionValue, count]
     */
    @Query(value = "SELECT DATE(t.call_time) AS call_date, t." + :dimension + " AS dimension_value, COUNT(*) AS call_count "
            + "FROM track_record t GROUP BY DATE(t.call_time), t." + :dimension
            + " ORDER BY call_date, dimension_value",
            nativeQuery = true)
    List<Object[]> countByDateAndDimension(@Param("dimension") String dimension);
}

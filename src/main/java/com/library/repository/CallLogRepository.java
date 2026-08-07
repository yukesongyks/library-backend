package com.library.repository;

import com.library.entity.CallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {

    @Query(value = """
        SELECT 'userType' AS dimension, COALESCE(c.user_type, 'UNKNOWN') AS value, COUNT(*) AS cnt
        FROM call_log c GROUP BY c.user_type
        UNION ALL
        SELECT 'userLevel', COALESCE(c.user_level, 'UNKNOWN'), COUNT(*)
        FROM call_log c GROUP BY c.user_level
        UNION ALL
        SELECT 'department', COALESCE(c.department, 'UNKNOWN'), COUNT(*)
        FROM call_log c GROUP BY c.department
        """, nativeQuery = true)
    List<Object[]> findAllByDimensions();
}

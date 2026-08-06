package com.library.backend.repository;

import com.library.backend.entity.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

    @Query("SELECT a.userType as label, COUNT(a) as count FROM ApiCallLog a " +
           "WHERE a.calledAt BETWEEN :startDate AND :endDate " +
           "AND (:apiName IS NULL OR a.apiName = :apiName) " +
           "GROUP BY a.userType ORDER BY count DESC")
    List<Object[]> countByUserType(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   @Param("apiName") String apiName);

    @Query("SELECT a.level as label, COUNT(a) as count FROM ApiCallLog a " +
           "WHERE a.calledAt BETWEEN :startDate AND :endDate " +
           "AND (:apiName IS NULL OR a.apiName = :apiName) " +
           "GROUP BY a.level ORDER BY count DESC")
    List<Object[]> countByLevel(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                @Param("apiName") String apiName);

    @Query("SELECT a.department as label, COUNT(a) as count FROM ApiCallLog a " +
           "WHERE a.calledAt BETWEEN :startDate AND :endDate " +
           "AND (:apiName IS NULL OR a.apiName = :apiName) " +
           "GROUP BY a.department ORDER BY count DESC")
    List<Object[]> countByDepartment(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("apiName") String apiName);

    List<ApiCallLog> findByCalledAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<ApiCallLog> findByApiNameAndCalledAtBetween(String apiName, LocalDateTime startDate, LocalDateTime endDate);
}

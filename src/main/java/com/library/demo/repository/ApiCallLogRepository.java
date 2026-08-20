package com.library.demo.repository;

import com.library.demo.model.entity.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

    List<ApiCallLog> findByApiTypeOrderByCreatedAtDesc(String apiType);

    List<ApiCallLog> findByApiTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
            String apiType, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a.department, COUNT(a) FROM ApiCallLog a " +
           "WHERE (:apiType = 'all' OR a.apiType = :apiType) " +
           "AND (:start IS NULL OR a.createdAt >= :start) " +
           "AND (:end IS NULL OR a.createdAt <= :end) " +
           "GROUP BY a.department ORDER BY COUNT(a) DESC")
    List<Object[]> countByDepartment(@Param("apiType") String apiType,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    @Query("SELECT a.personnelType, COUNT(a) FROM ApiCallLog a " +
           "WHERE (:apiType = 'all' OR a.apiType = :apiType) " +
           "AND (:start IS NULL OR a.createdAt >= :start) " +
           "AND (:end IS NULL OR a.createdAt <= :end) " +
           "GROUP BY a.personnelType ORDER BY COUNT(a) DESC")
    List<Object[]> countByPersonnelType(@Param("apiType") String apiType,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT a.personnelLevel, COUNT(a) FROM ApiCallLog a " +
           "WHERE (:apiType = 'all' OR a.apiType = :apiType) " +
           "AND (:start IS NULL OR a.createdAt >= :start) " +
           "AND (:end IS NULL OR a.createdAt <= :end) " +
           "GROUP BY a.personnelLevel ORDER BY COUNT(a) DESC")
    List<Object[]> countByPersonnelLevel(@Param("apiType") String apiType,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("SELECT CAST(a.createdAt AS DATE), a.department, COUNT(a) FROM ApiCallLog a " +
           "WHERE (:apiType = 'all' OR a.apiType = :apiType) " +
           "AND (:start IS NULL OR a.createdAt >= :start) " +
           "AND (:end IS NULL OR a.createdAt <= :end) " +
           "GROUP BY CAST(a.createdAt AS DATE), a.department ORDER BY CAST(a.createdAt AS DATE)")
    List<Object[]> timeSeriesByDepartment(@Param("apiType") String apiType,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT CAST(a.createdAt AS DATE), a.personnelType, COUNT(a) FROM ApiCallLog a " +
           "WHERE (:apiType = 'all' OR a.apiType = :apiType) " +
           "AND (:start IS NULL OR a.createdAt >= :start) " +
           "AND (:end IS NULL OR a.createdAt <= :end) " +
           "GROUP BY CAST(a.createdAt AS DATE), a.personnelType ORDER BY CAST(a.createdAt AS DATE)")
    List<Object[]> timeSeriesByPersonnelType(@Param("apiType") String apiType,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("SELECT CAST(a.createdAt AS DATE), a.personnelLevel, COUNT(a) FROM ApiCallLog a " +
           "WHERE (:apiType = 'all' OR a.apiType = :apiType) " +
           "AND (:start IS NULL OR a.createdAt >= :start) " +
           "AND (:end IS NULL OR a.createdAt <= :end) " +
           "GROUP BY CAST(a.createdAt AS DATE), a.personnelLevel ORDER BY CAST(a.createdAt AS DATE)")
    List<Object[]> timeSeriesByPersonnelLevel(@Param("apiType") String apiType,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) FROM ApiCallLog a WHERE a.createdAt >= :todayStart")
    Long countToday(@Param("todayStart") LocalDateTime todayStart);

    @Query("SELECT COUNT(DISTINCT a.userId) FROM ApiCallLog a")
    Long countDistinctUsers();

    @Query("SELECT AVG(a.durationMs) FROM ApiCallLog a")
    Double averageDuration();
}

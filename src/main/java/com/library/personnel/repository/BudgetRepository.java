package com.library.personnel.repository;

import com.library.personnel.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByEmployeeId(String employeeId);

    Optional<Budget> findByEmployeeIdAndYear(String employeeId, Integer year);

    List<Budget> findByEmployeeIdAndYearAndQuarter(String employeeId, Integer year, Integer quarter);

    List<Budget> findByEmployeeIdAndYearAndQuarterAndMonth(String employeeId, Integer year, Integer quarter, Integer month);

    @Query("SELECT b FROM Budget b WHERE b.employeeId = :employeeId AND b.year = :year " +
           "AND b.quarter IS NULL AND b.month IS NULL")
    Optional<Budget> findAnnualBudget(@Param("employeeId") String employeeId, @Param("year") Integer year);

    @Query("SELECT b FROM Budget b WHERE b.employeeId = :employeeId AND b.year = :year " +
           "AND b.quarter = :quarter AND b.month IS NULL")
    Optional<Budget> findQuarterlyBudget(@Param("employeeId") String employeeId,
                                          @Param("year") Integer year,
                                          @Param("quarter") Integer quarter);

    @Query("SELECT b FROM Budget b WHERE b.employeeId = :employeeId AND b.year = :year " +
           "AND b.quarter = :quarter AND b.month = :month")
    Optional<Budget> findMonthlyBudget(@Param("employeeId") String employeeId,
                                        @Param("year") Integer year,
                                        @Param("quarter") Integer quarter,
                                        @Param("month") Integer month);

    boolean existsByEmployeeIdAndYearAndQuarterAndMonth(String employeeId, Integer year, Integer quarter, Integer month);
}
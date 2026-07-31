package com.library.backend.repository;

import com.library.backend.entity.CostRecord;
import com.library.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CostRecordRepository extends JpaRepository<CostRecord, Long> {

    @Query("SELECT c FROM CostRecord c " +
           "LEFT JOIN FETCH c.department " +
           "LEFT JOIN FETCH c.employee " +
           "WHERE " +
           "(:departmentId IS NULL OR c.department.id = :departmentId) AND " +
           "(:businessLineId IS NULL OR c.businessLine.id = :businessLineId) AND " +
           "(:projectId IS NULL OR c.project.id = :projectId) AND " +
           "(:employeeId IS NULL OR c.employee.id = :employeeId) AND " +
           "(:costYear IS NULL OR c.costYear = :costYear) AND " +
           "(:costMonth IS NULL OR c.costMonth = :costMonth) AND " +
           "(:role IS NULL OR c.employee.role = :role)")
    List<CostRecord> findByFilters(
            @Param("departmentId") Long departmentId,
            @Param("businessLineId") Long businessLineId,
            @Param("projectId") Long projectId,
            @Param("employeeId") Long employeeId,
            @Param("costYear") Integer costYear,
            @Param("costMonth") Integer costMonth,
            @Param("role") Employee.EmployeeRole role);

    @Query("SELECT c.costYear, c.costMonth, SUM(c.amount) FROM CostRecord c " +
           "WHERE (:costYear IS NULL OR c.costYear = :costYear) " +
           "GROUP BY c.costYear, c.costMonth ORDER BY c.costYear, c.costMonth")
    List<Object[]> findMonthlyTrend(@Param("costYear") Integer costYear);

    @Query("SELECT c.employee.role, SUM(c.amount) FROM CostRecord c " +
           "WHERE (:costYear IS NULL OR c.costYear = :costYear) " +
           "GROUP BY c.employee.role")
    List<Object[]> findCostByRole(@Param("costYear") Integer costYear);

    @Query("SELECT c.project.id, c.project.name, SUM(c.amount) FROM CostRecord c " +
           "WHERE c.project IS NOT NULL AND (:costYear IS NULL OR c.costYear = :costYear) " +
           "GROUP BY c.project.id, c.project.name")
    List<Object[]> findCostByProject(@Param("costYear") Integer costYear);

    @Query("SELECT c.department.id, c.department.name, SUM(c.amount) FROM CostRecord c " +
           "WHERE (:costYear IS NULL OR c.costYear = :costYear) " +
           "GROUP BY c.department.id, c.department.name")
    List<Object[]> findCostByDepartment(@Param("costYear") Integer costYear);

    @Query("SELECT c.businessLine.id, c.businessLine.name, SUM(c.amount) FROM CostRecord c " +
           "WHERE (:costYear IS NULL OR c.costYear = :costYear) " +
           "GROUP BY c.businessLine.id, c.businessLine.name")
    List<Object[]> findCostByBusinessLine(@Param("costYear") Integer costYear);

    @Query("SELECT c.employee.id, c.employee.name, SUM(c.amount) FROM CostRecord c " +
           "WHERE (:costYear IS NULL OR c.costYear = :costYear) " +
           "GROUP BY c.employee.id, c.employee.name")
    List<Object[]> findCostByEmployee(@Param("costYear") Integer costYear);

    @Query("SELECT SUM(c.amount) FROM CostRecord c WHERE c.costYear = :year")
    Optional<BigDecimal> findTotalCostByYear(@Param("year") Integer year);
}

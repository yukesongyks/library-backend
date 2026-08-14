package com.library.personnel.repository;

import com.library.personnel.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByEmployeeIdAndDeletedFalse(String employeeId);

    boolean existsByEmployeeId(String employeeId);

    @Query("SELECT e FROM Employee e WHERE e.deleted = false AND " +
           "(LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Employee> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT MAX(e.employeeId) FROM Employee e WHERE e.employeeId LIKE 'EMP%'")
    Optional<String> findMaxEmployeeId();
}
package com.library.cost.mapper;

import com.library.cost.dto.LaborCostRow;
import com.library.cost.dto.ProjectCostRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CostReportMapper {

    @Select("""
            SELECT lc.project_id AS projectId, lc.month AS month, lc.amount AS amount,
                   e.role AS role, e.department_id AS departmentId,
                   p.business_line_id AS businessLineId, lc.employee_id AS employeeId
            FROM labor_cost lc
            JOIN employee e ON lc.employee_id = e.id
            JOIN project p ON lc.project_id = p.id
            WHERE lc.month LIKE CONCAT(#{year}, '%')
            """)
    List<LaborCostRow> selectLaborRows(@Param("year") String year);

    @Select("""
            SELECT pc.project_id AS projectId, pc.month AS month,
                   pc.actual_amount AS actualAmount,
                   p.department_id AS departmentId,
                   p.business_line_id AS businessLineId,
                   p.budget_amount AS budgetAmount
            FROM project_cost pc
            JOIN project p ON pc.project_id = p.id
            WHERE pc.month LIKE CONCAT(#{year}, '%')
            """)
    List<ProjectCostRow> selectProjectCostRows(@Param("year") String year);
}

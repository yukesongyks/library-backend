package com.library.backend.dto;

import com.library.backend.entity.Employee;
import lombok.Data;

@Data
public class CostQueryRequest {
    private Long departmentId;
    private Long businessLineId;
    private Long projectId;
    private Long employeeId;
    private Integer costYear;
    private Integer costMonth;
    private Integer quarter;
    private Employee.EmployeeRole role;
}

package com.library.backend.dto;

import com.library.backend.entity.Employee;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class CostQueryRequest {
    private Long departmentId;
    private Long businessLineId;
    private Long projectId;
    private Long employeeId;

    @Min(2000)
    @Max(2100)
    private Integer costYear;

    @Min(1)
    @Max(12)
    private Integer costMonth;

    @Min(1)
    @Max(4)
    private Integer quarter;

    private Employee.EmployeeRole role;
}

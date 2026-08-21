package com.library.cost.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("labor_cost")
public class LaborCost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private Long projectId;
    private String month;
    private BigDecimal amount;
}
package com.library.cost.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("project_cost")
public class ProjectCost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String month;
    private BigDecimal actualAmount;
}
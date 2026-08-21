package com.library.cost.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("business_line")
public class BusinessLine {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
}
package com.library.demo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("demo_call_log")
public class DemoCallLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String apiType;

    private String callerId;

    private String callerName;

    private String personType;

    private String personLevel;

    private String department;

    private String requestParams;

    private String responseData;

    private Integer executionTimeMs;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime callTime;
}

package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("api_metrics")
public class ApiMetrics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String apiPath;
    private String callerId;
    private String callerName;
    private String callerType;
    private String callerLevel;
    private String callerDept;
    private LocalDateTime callTime;
    private Integer durationMs;
    private Integer success;
}
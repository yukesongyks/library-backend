package com.library.backend.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_call_log", indexes = {
    @Index(name = "idx_api_call_log_dimension", columnList = "user_type, level, department, api_name, called_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelProperty("ID")
    private Long id;

    @Column(name = "user_id", nullable = false, length = 64)
    @ExcelProperty("用户ID")
    private String userId;

    @Column(name = "user_type", nullable = false, length = 32)
    @ExcelProperty("用户类型")
    private String userType;

    @Column(name = "level", length = 32)
    @ExcelProperty("层级")
    private String level;

    @Column(name = "department", length = 64)
    @ExcelProperty("部门")
    private String department;

    @Column(name = "api_name", nullable = false, length = 32)
    @ExcelProperty("接口名称")
    private String apiName;

    @Column(name = "called_at", nullable = false)
    @ExcelProperty("调用时间")
    private LocalDateTime calledAt;

    @PrePersist
    protected void onCreate() {
        if (calledAt == null) {
            calledAt = LocalDateTime.now();
        }
    }
}

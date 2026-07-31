package com.example.library.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * API 调用日志实体：埋点落库，记录三接口每次调用的维度快照与耗时。
 * 对应 design.md 2.2 / tasks B2。
 */
@Entity
@Table(name = "api_call_log", indexes = {
        @Index(name = "idx_api_name", columnList = "apiName"),
        @Index(name = "idx_called_at", columnList = "calledAt"),
        @Index(name = "idx_user_type", columnList = "userType"),
        @Index(name = "idx_user_level", columnList = "userLevel"),
        @Index(name = "idx_department", columnList = "department")
})
public class ApiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String apiName;

    private Long userId;

    @Column(length = 50)
    private String username;

    @Column(length = 30)
    private String userType;

    @Column(length = 20)
    private String userLevel;

    @Column(length = 100)
    private String department;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(nullable = false)
    private Long durationMs;

    @Column(nullable = false)
    private LocalDateTime calledAt;

    protected ApiCallLog() {
    }

    public ApiCallLog(String apiName, Long userId, String username, String userType,
                      String userLevel, String department, String status,
                      Long durationMs, LocalDateTime calledAt) {
        this.apiName = apiName;
        this.userId = userId;
        this.username = username;
        this.userType = userType;
        this.userLevel = userLevel;
        this.department = department;
        this.status = status;
        this.durationMs = durationMs;
        this.calledAt = calledAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }
}

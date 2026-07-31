package com.library.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import java.time.LocalDateTime;

/**
 * 调用埋点记录。
 *
 * <p>每次算法接口调用（成功或异常）由 {@code CallLogAspect} 写入一条记录。
 * 人员维度字段冗余存储，避免分析报表频繁 JOIN；用户维度变更不影响历史记录。</p>
 *
 * <p>{@code responseStatus} 取值 {@code SUCCESS} 或 {@code ERROR}：
 * spec {@code tracking-analytics.md} 要求接口异常时同样记录埋点且 status=ERROR。</p>
 */
@Entity
@Table(name = "call_logs")
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 接口类型：helloworld / hash / bubble-sort */
    @Column(nullable = false, length = 50)
    private String apiType;

    @Column(name = "caller_user_id")
    private Long callerUserId;

    @Column(name = "caller_username", length = 50)
    private String callerUsername;

    /** 冗余存储：人员类型 */
    @Column(name = "personnel_type", length = 50)
    private String personnelType;

    /** 冗余存储：人员层级 */
    @Column(name = "personnel_level", length = 50)
    private String personnelLevel;

    /** 冗余存储：部门 */
    @Column(length = 100)
    private String department;

    @Column(name = "called_at", nullable = false)
    private LocalDateTime calledAt;

    @Column(name = "request_summary", length = 500)
    private String requestSummary;

    /** SUCCESS / ERROR */
    @Column(name = "response_status", length = 20)
    private String responseStatus;

    @Column(name = "response_data", length = 2000)
    private String responseData;

    @PrePersist
    void onCreate() {
        this.calledAt = LocalDateTime.now();
    }

    // ---- getters / setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public Long getCallerUserId() {
        return callerUserId;
    }

    public void setCallerUserId(Long callerUserId) {
        this.callerUserId = callerUserId;
    }

    public String getCallerUsername() {
        return callerUsername;
    }

    public void setCallerUsername(String callerUsername) {
        this.callerUsername = callerUsername;
    }

    public String getPersonnelType() {
        return personnelType;
    }

    public void setPersonnelType(String personnelType) {
        this.personnelType = personnelType;
    }

    public String getPersonnelLevel() {
        return personnelLevel;
    }

    public void setPersonnelLevel(String personnelLevel) {
        this.personnelLevel = personnelLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }

    public String getRequestSummary() {
        return requestSummary;
    }

    public void setRequestSummary(String requestSummary) {
        this.requestSummary = requestSummary;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}

package com.example.library.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invocation_events", indexes = {
        @Index(name = "idx_invocation_called_at", columnList = "called_at"),
        @Index(name = "idx_invocation_algorithm", columnList = "algorithm_type")
})
public class InvocationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, unique = true, length = 64)
    private String requestId;
    @Enumerated(EnumType.STRING)
    @Column(name = "algorithm_type", nullable = false, length = 32)
    private AlgorithmType algorithmType;
    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;
    @Column(name = "user_type", nullable = false, length = 64)
    private String userType;
    @Column(name = "user_level", nullable = false, length = 64)
    private String userLevel;
    @Column(name = "department_id", nullable = false, length = 128)
    private String departmentId;
    @Column(name = "department_name", nullable = false, length = 128)
    private String departmentName;
    @Column(name = "called_at", nullable = false)
    private LocalDateTime calledAt;
    @Column(nullable = false)
    private boolean success;
    @Column(name = "error_code", length = 64)
    private String errorCode;
    @Column(nullable = false)
    private long durationMs;
    @Lob
    @Column(name = "result_payload")
    private String resultPayload;

    protected InvocationEvent() {}

    public InvocationEvent(String requestId, AlgorithmType algorithmType, UserSnapshot user,
                            LocalDateTime calledAt, boolean success, String errorCode,
                            long durationMs, String resultPayload) {
        this.requestId = requestId;
        this.algorithmType = algorithmType;
        this.userId = user.userId();
        this.userType = user.userType();
        this.userLevel = user.userLevel();
        this.departmentId = user.departmentId();
        this.departmentName = user.departmentName();
        this.calledAt = calledAt;
        this.success = success;
        this.errorCode = errorCode;
        this.durationMs = durationMs;
        this.resultPayload = resultPayload;
    }

    public Long getId() { return id; }
    public String getRequestId() { return requestId; }
    public AlgorithmType getAlgorithmType() { return algorithmType; }
    public String getUserId() { return userId; }
    public String getUserType() { return userType; }
    public String getUserLevel() { return userLevel; }
    public String getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public LocalDateTime getCalledAt() { return calledAt; }
    public boolean isSuccess() { return success; }
    public String getErrorCode() { return errorCode; }
    public long getDurationMs() { return durationMs; }
    public String getResultPayload() { return resultPayload; }
}

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
 * 用户实体。
 *
 * <p>承载埋点所需的调用人员维度信息：人员类型、人员层级、部门。
 * 调用人身份通过请求头 {@code X-User-Id} 传入，此处按 id 关联获取维度。</p>
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 人员类型：开发 / 测试 / 产品 / 运维 */
    @Column(name = "personnel_type", length = 50)
    private String personnelType;

    /** 人员层级：P5 / P6 / P7 / M1 / M2 */
    @Column(name = "personnel_level", length = 50)
    private String personnelLevel;

    /** 部门：技术部 / 产品部 / 运维部 */
    @Column(length = 100)
    private String department;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ---- getters / setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

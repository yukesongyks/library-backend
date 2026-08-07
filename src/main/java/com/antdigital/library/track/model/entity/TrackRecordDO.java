package com.antdigital.library.track.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 调用埋点记录实体。
 *
 * @author library-backend
 */
@Entity
@Table(name = "track_record")
public class TrackRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 被调用的接口路径 */
    @Column(name = "api_path")
    private String apiPath;

    /** 调用人ID */
    @Column(name = "user_id")
    private String userId;

    /** 调用人姓名 */
    @Column(name = "user_name")
    private String userName;

    /** 人员类型（如：普通用户、管理员、访客） */
    @Column(name = "user_type")
    private String userType;

    /** 人员层级（如：L1、L2、L3） */
    @Column(name = "user_level")
    private String userLevel;

    /** 人员部门 */
    @Column(name = "user_department")
    private String userDepartment;

    /** 调用时间 */
    @Column(name = "call_time")
    private LocalDateTime callTime;

    public TrackRecordDO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public LocalDateTime getCallTime() {
        return callTime;
    }

    public void setCallTime(LocalDateTime callTime) {
        this.callTime = callTime;
    }

    @Override
    public String toString() {
        return "TrackRecordDO{"
                + "id=" + id
                + ", apiPath='" + apiPath + '\''
                + ", userId='" + userId + '\''
                + ", userName='" + userName + '\''
                + ", userType='" + userType + '\''
                + ", userLevel='" + userLevel + '\''
                + ", userDepartment='" + userDepartment + '\''
                + ", callTime=" + callTime
                + '}';
    }
}

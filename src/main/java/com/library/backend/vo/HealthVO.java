package com.library.backend.vo;

/**
 * 健康检查响应 VO
 */
public class HealthVO {

    private String status;
    private String uptime;
    private String dbConnection;

    public HealthVO() {
    }

    public HealthVO(String status, String uptime, String dbConnection) {
        this.status = status;
        this.uptime = uptime;
        this.dbConnection = dbConnection;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getDbConnection() {
        return dbConnection;
    }

    public void setDbConnection(String dbConnection) {
        this.dbConnection = dbConnection;
    }
}
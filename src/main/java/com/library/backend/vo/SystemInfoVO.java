package com.library.backend.vo;

/**
 * 系统信息响应 VO
 */
public class SystemInfoVO {

    private String name;
    private String version;
    private String description;
    private String techStack;
    private String model;
    private String timestamp;

    public SystemInfoVO() {
    }

    public SystemInfoVO(String name, String version, String description,
                        String techStack, String model, String timestamp) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.techStack = techStack;
        this.model = model;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
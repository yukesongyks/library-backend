package com.library.reader.dto;

import com.library.auth.Role;
import com.library.reader.Reader;

import java.time.LocalDateTime;

/**
 * 读者信息响应 VO（不含密码哈希）。
 */
public class ReaderVO {

    private Long id;
    private String name;
    private String username;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReaderVO() {
    }

    public ReaderVO(Reader r) {
        this.id = r.getId();
        this.name = r.getName();
        this.username = r.getUsername();
        this.role = r.getRole();
        this.enabled = r.getEnabled();
        this.createdAt = r.getCreatedAt();
        this.updatedAt = r.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package com.library.personnel.dto.response;

import com.library.personnel.entity.Whitelist;
import com.library.personnel.enums.WhitelistType;

import java.time.LocalDateTime;

public class WhitelistResponse {

    private Long id;
    private WhitelistType type;
    private String employeeId;
    private String name;
    private String note;
    private LocalDateTime addedAt;

    public static WhitelistResponse fromEntity(Whitelist whitelist) {
        WhitelistResponse response = new WhitelistResponse();
        response.setId(whitelist.getId());
        response.setType(whitelist.getType());
        response.setEmployeeId(whitelist.getEmployeeId());
        response.setName(whitelist.getName());
        response.setNote(whitelist.getNote());
        response.setAddedAt(whitelist.getAddedAt());
        return response;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WhitelistType getType() {
        return type;
    }

    public void setType(WhitelistType type) {
        this.type = type;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
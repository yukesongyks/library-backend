package com.library.personnel.entity;

import com.library.personnel.enums.WhitelistType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "whitelist", uniqueConstraints = {
    @UniqueConstraint(name = "uk_type_employee", columnNames = {"type", "employee_id"})
}, indexes = {
    @Index(name = "idx_whitelist_type", columnList = "type"),
    @Index(name = "idx_whitelist_employee", columnList = "employee_id")
})
public class Whitelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private WhitelistType type;

    @Column(name = "employee_id", length = 20, nullable = false)
    private String employeeId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
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
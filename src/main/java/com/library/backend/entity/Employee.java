package com.library.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "cost_employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_line_id")
    private BusinessLine businessLine;

    public enum EmployeeRole {
        DEVELOPER, TESTER, PRODUCT, OPS
    }
}

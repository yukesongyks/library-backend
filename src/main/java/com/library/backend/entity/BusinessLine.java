package com.library.backend.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "cost_business_line")
public class BusinessLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;
}

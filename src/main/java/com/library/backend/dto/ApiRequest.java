package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest {
    private String userId;
    private String userType; // EMPLOYEE | CONTRACTOR | INTERN
    private String level;
    private String department;

    // For hash API (optional)
    private String input;

    // For bubble-sort API (optional)
    private List<Integer> array;
}

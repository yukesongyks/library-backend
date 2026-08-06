package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsQuery {
    private String dimension; // userType | level | department
    private String startDate;
    private String endDate;
    private String apiName;
}

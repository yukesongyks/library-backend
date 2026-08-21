package com.library.cost.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisQuery {
    private String dimension;
    private String year;
    private String month;
    private String quarter;
    private String role;
}

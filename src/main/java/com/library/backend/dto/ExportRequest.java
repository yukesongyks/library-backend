package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {
    private String tab; // hello | hash | bubble-sort
    private String startDate;
    private String endDate;
    private String apiName;
}

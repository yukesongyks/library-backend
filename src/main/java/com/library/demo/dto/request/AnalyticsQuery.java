package com.library.demo.dto.request;

import lombok.Data;

@Data
public class AnalyticsQuery {
    private String dimension; // PERSON_TYPE|PERSON_LEVEL|DEPARTMENT|DATE
    private String apiType;   // HELLOWORLD|HASH|BUBBLE_SORT (optional)
    private String startDate; // yyyy-MM-dd
    private String endDate;   // yyyy-MM-dd
    private String granularity; // DAY|WEEK|MONTH (for trend)
}

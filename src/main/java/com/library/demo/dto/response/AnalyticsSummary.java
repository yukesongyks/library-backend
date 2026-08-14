package com.library.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummary {
    private String dimension;
    private List<SummaryItem> items;
    private long totalCount;
    private DateRange dateRange;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryItem {
        private String label;
        private long count;
        private double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        private String start;
        private String end;
    }
}

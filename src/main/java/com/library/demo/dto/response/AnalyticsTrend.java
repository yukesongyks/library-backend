package com.library.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsTrend {
    private String granularity;
    private List<Series> series;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Series {
        private String apiType;
        private List<Point> points;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private String date;
        private long count;
    }
}

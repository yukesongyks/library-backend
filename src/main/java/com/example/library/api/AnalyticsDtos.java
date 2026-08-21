package com.example.library.api;

import com.example.library.domain.AlgorithmType;

import java.time.LocalDate;
import java.util.List;

public final class AnalyticsDtos {
    private AnalyticsDtos() {}

    public record AnalyticsFilter(LocalDate from, LocalDate to, AlgorithmType algorithmType,
                                  String userType, String userLevel, String departmentId) {}

    public record Summary(long totalCalls, long successfulCalls, long failedCalls, long uniqueUsers) {}

    public record TimePoint(String label, long count) {}

    public record BreakdownItem(String key, String label, long count) {}

    public record AnalyticsReport(Summary summary, List<TimePoint> timeseries,
                                  List<BreakdownItem> byUserType, List<BreakdownItem> byUserLevel,
                                  List<BreakdownItem> byDepartment) {}
}

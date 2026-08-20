package com.library.demo.service;

import com.library.demo.model.response.AnalyticsResponse;
import com.library.demo.model.response.AnalyticsResponse.GroupItem;
import com.library.demo.model.response.AnalyticsResponse.TimeSeriesItem;
import com.library.demo.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ApiCallLogRepository repository;

    public AnalyticsService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    public AnalyticsResponse getAnalytics(String dimension, String apiType,
                                          String startDate, String endDate) {
        String effectiveApiType = (apiType == null || apiType.isBlank()) ? "all" : apiType;
        String effectiveDimension = (dimension == null || dimension.isBlank()) ? "department" : dimension;

        LocalDateTime start = (startDate != null && !startDate.isBlank())
                ? LocalDate.parse(startDate).atStartOfDay() : null;
        LocalDateTime end = (endDate != null && !endDate.isBlank())
                ? LocalDate.parse(endDate).atTime(LocalTime.MAX) : null;

        AnalyticsResponse response = new AnalyticsResponse();
        response.setDimension(effectiveDimension);
        response.setApiType(effectiveApiType);

        // Summary stats
        response.setTotalCalls(repository.count());
        Long todayCalls = repository.countToday(LocalDate.now().atStartOfDay());
        response.setTodayCalls(todayCalls != null ? todayCalls : 0L);
        Long activeUsers = repository.countDistinctUsers();
        response.setActiveUsers(activeUsers != null ? activeUsers : 0L);
        Double avgDuration = repository.averageDuration();
        response.setAvgDurationMs(avgDuration != null ? Math.round(avgDuration * 100.0) / 100.0 : 0.0);

        // Group data
        List<Object[]> groupData = fetchGroupData(effectiveDimension, effectiveApiType, start, end);
        long total = groupData.stream().mapToLong(row -> (Long) row[1]).sum();
        List<GroupItem> groups = groupData.stream()
                .map(row -> new GroupItem(
                        row[0] != null ? row[0].toString() : "Unknown",
                        (Long) row[1],
                        total > 0 ? Math.round(((Long) row[1]) * 10000.0 / total) / 100.0 : 0.0))
                .collect(Collectors.toList());
        response.setGroups(groups);

        // Time series data
        List<Object[]> tsData = fetchTimeSeriesData(effectiveDimension, effectiveApiType, start, end);
        Map<String, Map<String, Long>> dateMap = new LinkedHashMap<>();
        for (Object[] row : tsData) {
            String date = row[0].toString();
            String groupName = row[1] != null ? row[1].toString() : "Unknown";
            Long count = (Long) row[2];
            dateMap.computeIfAbsent(date, k -> new LinkedHashMap<>()).put(groupName, count);
        }
        List<TimeSeriesItem> timeSeries = dateMap.entrySet().stream()
                .map(e -> new TimeSeriesItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        response.setTimeSeries(timeSeries);

        return response;
    }

    private List<Object[]> fetchGroupData(String dimension, String apiType,
                                          LocalDateTime start, LocalDateTime end) {
        return switch (dimension) {
            case "personnelType" -> repository.countByPersonnelType(apiType, start, end);
            case "personnelLevel" -> repository.countByPersonnelLevel(apiType, start, end);
            default -> repository.countByDepartment(apiType, start, end);
        };
    }

    private List<Object[]> fetchTimeSeriesData(String dimension, String apiType,
                                               LocalDateTime start, LocalDateTime end) {
        return switch (dimension) {
            case "personnelType" -> repository.timeSeriesByPersonnelType(apiType, start, end);
            case "personnelLevel" -> repository.timeSeriesByPersonnelLevel(apiType, start, end);
            default -> repository.timeSeriesByDepartment(apiType, start, end);
        };
    }
}

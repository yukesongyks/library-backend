package com.library.demo.model.response;

import java.util.List;
import java.util.Map;

public class AnalyticsResponse {
    private String dimension;
    private String apiType;
    private long totalCalls;
    private long todayCalls;
    private long activeUsers;
    private double avgDurationMs;
    private List<GroupItem> groups;
    private List<TimeSeriesItem> timeSeries;

    // Getters and Setters
    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public String getApiType() { return apiType; }
    public void setApiType(String apiType) { this.apiType = apiType; }
    public long getTotalCalls() { return totalCalls; }
    public void setTotalCalls(long totalCalls) { this.totalCalls = totalCalls; }
    public long getTodayCalls() { return todayCalls; }
    public void setTodayCalls(long todayCalls) { this.todayCalls = todayCalls; }
    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
    public double getAvgDurationMs() { return avgDurationMs; }
    public void setAvgDurationMs(double avgDurationMs) { this.avgDurationMs = avgDurationMs; }
    public List<GroupItem> getGroups() { return groups; }
    public void setGroups(List<GroupItem> groups) { this.groups = groups; }
    public List<TimeSeriesItem> getTimeSeries() { return timeSeries; }
    public void setTimeSeries(List<TimeSeriesItem> timeSeries) { this.timeSeries = timeSeries; }

    public static class GroupItem {
        private String name;
        private long count;
        private double percentage;

        public GroupItem(String name, long count, double percentage) {
            this.name = name;
            this.count = count;
            this.percentage = percentage;
        }

        public String getName() { return name; }
        public long getCount() { return count; }
        public double getPercentage() { return percentage; }
    }

    public static class TimeSeriesItem {
        private String date;
        private Map<String, Long> groups;

        public TimeSeriesItem(String date, Map<String, Long> groups) {
            this.date = date;
            this.groups = groups;
        }

        public String getDate() { return date; }
        public Map<String, Long> getGroups() { return groups; }
    }
}

package com.example.library.service;

import com.example.library.api.AnalyticsDtos;
import com.example.library.domain.AlgorithmType;
import com.example.library.domain.InvocationEvent;
import com.example.library.repository.InvocationEventRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ISO_LOCAL_DATE;
    private final InvocationEventRepository repository;

    public AnalyticsService(InvocationEventRepository repository) {
        this.repository = repository;
    }

    public AnalyticsDtos.AnalyticsReport report(AnalyticsDtos.AnalyticsFilter filter) {
        List<InvocationEvent> events = filtered(filter);
        AnalyticsDtos.Summary summary = new AnalyticsDtos.Summary(
                events.size(), events.stream().filter(InvocationEvent::isSuccess).count(),
                events.stream().filter(event -> !event.isSuccess()).count(),
                events.stream().map(InvocationEvent::getUserId).distinct().count());
        return new AnalyticsDtos.AnalyticsReport(summary,
                timeseries(events),
                group(events, InvocationEvent::getUserType, InvocationEvent::getUserType),
                group(events, InvocationEvent::getUserLevel, InvocationEvent::getUserLevel),
                group(events, InvocationEvent::getDepartmentId, InvocationEvent::getDepartmentName));
    }

    public byte[] export(AnalyticsDtos.AnalyticsFilter filter, String scope) {
        List<InvocationEvent> events = filtered(filter);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8))) {
            writer.write('\uFEFF');
            if ("algorithm".equalsIgnoreCase(scope)) {
                writer.println("requestId,algorithmType,calledAt,userId,userType,userLevel,departmentName,success,durationMs,resultPayload");
                events.forEach(event -> writer.println(csv(event.getRequestId(), event.getAlgorithmType(), event.getCalledAt(),
                        event.getUserId(), event.getUserType(), event.getUserLevel(), event.getDepartmentName(),
                        event.isSuccess(), event.getDurationMs(), event.getResultPayload())));
            } else {
                AnalyticsDtos.AnalyticsReport report = report(filter);
                writer.println("metric,value");
                writer.println(csv("totalCalls", report.summary().totalCalls()));
                writer.println(csv("successfulCalls", report.summary().successfulCalls()));
                writer.println(csv("failedCalls", report.summary().failedCalls()));
                writer.println(csv("uniqueUsers", report.summary().uniqueUsers()));
                writer.println();
                writer.println("dimension,key,label,count");
                report.timeseries().forEach(item -> writer.println(csv("timeseries", item.label(), item.label(), item.count())));
                report.byUserType().forEach(item -> writer.println(csv("userType", item.key(), item.label(), item.count())));
                report.byUserLevel().forEach(item -> writer.println(csv("userLevel", item.key(), item.label(), item.count())));
                report.byDepartment().forEach(item -> writer.println(csv("department", item.key(), item.label(), item.count())));
            }
        }
        return output.toByteArray();
    }

    private List<InvocationEvent> filtered(AnalyticsDtos.AnalyticsFilter filter) {
        LocalDate from = filter.from() == null ? LocalDate.now().minusDays(29) : filter.from();
        LocalDate to = filter.to() == null ? LocalDate.now() : filter.to();
        if (to.isBefore(from)) throw new IllegalArgumentException("to must be on or after from");
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);
        return repository.findByCalledAtBetweenOrderByCalledAtAsc(start, end).stream()
                .filter(event -> filter.algorithmType() == null || event.getAlgorithmType() == filter.algorithmType())
                .filter(event -> blankOrEquals(filter.userType(), event.getUserType()))
                .filter(event -> blankOrEquals(filter.userLevel(), event.getUserLevel()))
                .filter(event -> blankOrEquals(filter.departmentId(), event.getDepartmentId()))
                .toList();
    }

    private boolean blankOrEquals(String expected, String actual) {
        return expected == null || expected.isBlank() || expected.equals(actual);
    }

    private List<AnalyticsDtos.TimePoint> timeseries(List<InvocationEvent> events) {
        return events.stream()
                .collect(Collectors.groupingBy(event -> event.getCalledAt().toLocalDate().format(DAY),
                        LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new AnalyticsDtos.TimePoint(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<AnalyticsDtos.BreakdownItem> group(List<InvocationEvent> events,
                                                      Function<InvocationEvent, String> keyExtractor,
                                                      Function<InvocationEvent, String> labelExtractor) {
        Map<String, List<InvocationEvent>> grouped = events.stream().collect(Collectors.groupingBy(
                keyExtractor, LinkedHashMap::new, Collectors.toList()));
        return grouped.entrySet().stream()
                .map(entry -> new AnalyticsDtos.BreakdownItem(entry.getKey(),
                        entry.getValue().stream().map(labelExtractor).findFirst().orElse(entry.getKey()),
                        entry.getValue().size()))
                .sorted(Comparator.comparingLong(AnalyticsDtos.BreakdownItem::count).reversed())
                .toList();
    }

    private String csv(Object... values) {
        return java.util.Arrays.stream(values).map(value -> {
            String text = String.valueOf(value == null ? "" : value).replace("\"", "\"\"");
            return "\"" + text + "\"";
        }).collect(Collectors.joining(","));
    }
}

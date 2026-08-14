package com.library.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.demo.dto.request.AnalyticsQuery;
import com.library.demo.dto.response.AnalyticsSummary;
import com.library.demo.dto.response.AnalyticsTrend;
import com.library.demo.entity.DemoCallLog;
import com.library.demo.mapper.DemoCallLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final DemoCallLogMapper callLogMapper;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");
    private static final int MAX_QUERY_LIMIT = 50000;

    public AnalyticsSummary getSummary(AnalyticsQuery query) {
        LambdaQueryWrapper<DemoCallLog> wrapper = buildBaseQuery(query);
        wrapper.last("LIMIT " + MAX_QUERY_LIMIT);

        List<DemoCallLog> logs = callLogMapper.selectList(wrapper);

        String dimension = query.getDimension() != null ? query.getDimension() : "DEPARTMENT";
        Map<String, Long> grouped;

        switch (dimension) {
            case "PERSON_TYPE":
                grouped = logs.stream()
                        .filter(l -> l.getPersonType() != null)
                        .collect(Collectors.groupingBy(DemoCallLog::getPersonType, Collectors.counting()));
                break;
            case "PERSON_LEVEL":
                grouped = logs.stream()
                        .filter(l -> l.getPersonLevel() != null)
                        .collect(Collectors.groupingBy(DemoCallLog::getPersonLevel, Collectors.counting()));
                break;
            case "DATE":
                grouped = logs.stream()
                        .collect(Collectors.groupingBy(
                                l -> l.getCallTime().toLocalDate().format(DATE_FMT),
                                Collectors.counting()));
                break;
            case "DEPARTMENT":
            default:
                grouped = logs.stream()
                        .filter(l -> l.getDepartment() != null)
                        .collect(Collectors.groupingBy(DemoCallLog::getDepartment, Collectors.counting()));
                break;
        }

        long totalCount = grouped.values().stream().mapToLong(Long::longValue).sum();
        List<AnalyticsSummary.SummaryItem> items = grouped.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new AnalyticsSummary.SummaryItem(
                        e.getKey(),
                        e.getValue(),
                        totalCount > 0 ? Math.round(e.getValue() * 1000.0 / totalCount) / 10.0 : 0.0
                ))
                .collect(Collectors.toList());

        AnalyticsSummary summary = new AnalyticsSummary();
        summary.setDimension(dimension);
        summary.setItems(items);
        summary.setTotalCount(totalCount);
        summary.setDateRange(new AnalyticsSummary.DateRange(
                query.getStartDate() != null ? query.getStartDate() : LocalDate.now(ZONE_SHANGHAI).minusMonths(1).format(DATE_FMT),
                query.getEndDate() != null ? query.getEndDate() : LocalDate.now(ZONE_SHANGHAI).format(DATE_FMT)
        ));

        return summary;
    }

    public AnalyticsTrend getTrend(AnalyticsQuery query) {
        LambdaQueryWrapper<DemoCallLog> wrapper = buildBaseQuery(query);
        wrapper.last("LIMIT " + MAX_QUERY_LIMIT);
        List<DemoCallLog> logs = callLogMapper.selectList(wrapper);

        String granularity = query.getGranularity() != null ? query.getGranularity() : "DAY";

        // Group by apiType then by time bucket based on granularity
        Map<String, Map<String, Long>> byApiAndDate = logs.stream()
                .collect(Collectors.groupingBy(
                        DemoCallLog::getApiType,
                        Collectors.groupingBy(
                                l -> formatTimeBucket(l.getCallTime().toLocalDate(), granularity),
                                Collectors.counting()
                        )
                ));

        List<AnalyticsTrend.Series> seriesList = byApiAndDate.entrySet().stream()
                .map(entry -> {
                    List<AnalyticsTrend.Point> points = entry.getValue().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .map(e -> new AnalyticsTrend.Point(e.getKey(), e.getValue()))
                            .collect(Collectors.toList());
                    return new AnalyticsTrend.Series(entry.getKey(), points);
                })
                .collect(Collectors.toList());

        AnalyticsTrend trend = new AnalyticsTrend();
        trend.setGranularity(granularity);
        trend.setSeries(seriesList);
        return trend;
    }

    /**
     * 根据粒度格式化日期为对应的时间桶标签。
     *
     * @param date        日期
     * @param granularity 粒度：DAY / WEEK / MONTH
     * @return 时间桶标签字符串
     */
    private String formatTimeBucket(LocalDate date, String granularity) {
        switch (granularity) {
            case "WEEK":
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                int year = date.get(weekFields.weekBasedYear());
                return year + "-W" + String.format("%02d", weekNumber);
            case "MONTH":
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            case "DAY":
            default:
                return date.format(DATE_FMT);
        }
    }

    private LambdaQueryWrapper<DemoCallLog> buildBaseQuery(AnalyticsQuery query) {
        LambdaQueryWrapper<DemoCallLog> wrapper = new LambdaQueryWrapper<>();

        if (query.getApiType() != null && !query.getApiType().isBlank()) {
            wrapper.eq(DemoCallLog::getApiType, query.getApiType());
        }

        if (query.getStartDate() != null && !query.getStartDate().isBlank()) {
            LocalDateTime startDateTime = LocalDate.parse(query.getStartDate(), DATE_FMT).atStartOfDay();
            wrapper.ge(DemoCallLog::getCallTime, startDateTime);
        }

        if (query.getEndDate() != null && !query.getEndDate().isBlank()) {
            LocalDateTime endDateTime = LocalDate.parse(query.getEndDate(), DATE_FMT).atTime(LocalTime.MAX);
            wrapper.le(DemoCallLog::getCallTime, endDateTime);
        }

        return wrapper;
    }
}

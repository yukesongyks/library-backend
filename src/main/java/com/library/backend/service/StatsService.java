package com.library.backend.service;

import com.library.backend.dto.StatsItem;
import com.library.backend.dto.StatsResponse;
import com.library.backend.entity.ApiCallLog;
import com.library.backend.repository.ApiCallLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {
    
    private final ApiCallLogRepository apiCallLogRepository;
    
    public StatsResponse getStats(String dimension, String startDate, String endDate, String apiName) {
        LocalDateTime start = parseDateTime(startDate, true);
        LocalDateTime end = parseDateTime(endDate, false);
        
        List<ApiCallLog> logs;
        if (apiName != null && !apiName.isEmpty()) {
            logs = apiCallLogRepository.findByApiNameAndCalledAtBetween(apiName, start, end);
        } else {
            logs = apiCallLogRepository.findByCalledAtBetween(start, end);
        }
        
        Map<String, Long> grouped = switch (dimension) {
            case "userType" -> logs.stream()
                    .collect(Collectors.groupingBy(ApiCallLog::getUserType, Collectors.counting()));
            case "level" -> logs.stream()
                    .collect(Collectors.groupingBy(l -> l.getLevel() != null ? l.getLevel() : "UNKNOWN", Collectors.counting()));
            case "department" -> logs.stream()
                    .collect(Collectors.groupingBy(l -> l.getDepartment() != null ? l.getDepartment() : "UNKNOWN", Collectors.counting()));
            default -> throw new IllegalArgumentException("Unknown dimension: " + dimension);
        };
        
        List<StatsItem> items = grouped.entrySet().stream()
                .map(e -> StatsItem.builder()
                        .label(e.getKey())
                        .count(e.getValue())
                        .build())
                .collect(Collectors.toList());
        
        return StatsResponse.builder()
                .dimension(dimension)
                .items(items)
                .build();
    }
    
    private LocalDateTime parseDateTime(String dateStr, boolean isStart) {
        if (dateStr == null || dateStr.isEmpty()) {
            return isStart ? LocalDateTime.now().minusDays(30) : LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.parse(dateStr + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}

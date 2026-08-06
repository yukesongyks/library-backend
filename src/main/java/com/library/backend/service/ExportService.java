package com.library.backend.service;

import com.alibaba.excel.EasyExcel;
import com.library.backend.entity.ApiCallLog;
import com.library.backend.repository.ApiCallLogRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {
    
    private final ApiCallLogRepository apiCallLogRepository;
    
    public void export(String tab, String startDate, String endDate, String apiName, HttpServletResponse response) throws IOException {
        LocalDateTime start = parseDateTime(startDate, true);
        LocalDateTime end = parseDateTime(endDate, false);
        
        List<ApiCallLog> logs;
        if (apiName != null && !apiName.isEmpty()) {
            logs = apiCallLogRepository.findByApiNameAndCalledAtBetween(apiName, start, end);
        } else {
            logs = apiCallLogRepository.findByCalledAtBetween(start, end);
        }
        
        // Filter by tab/api name if specified
        if (tab != null && !tab.isEmpty()) {
            String targetApi = switch (tab) {
                case "hello" -> "hello";
                case "hash" -> "hash";
                case "bubble-sort" -> "bubble-sort";
                default -> null;
            };
            if (targetApi != null) {
                logs = logs.stream()
                        .filter(l -> targetApi.equals(l.getApiName()))
                        .toList();
            }
        }
        
        String fileName = URLEncoder.encode(tab + "_export_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        
        EasyExcel.write(response.getOutputStream(), ApiCallLog.class)
                .sheet("API调用记录")
                .doWrite(logs);
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

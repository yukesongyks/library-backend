package com.example.library.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service for exporting algorithm demonstration results as JSON or CSV files.
 */
@Service
public class ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String JSON_TYPE = "application/json";
    private static final String CSV_TYPE = "text/csv";

    /**
     * Exports the given data in the specified type and format.
     *
     * @param type   export type: "helloworld", "hash", or "bubblesort"
     * @param data   the data to export
     * @param format export format: "json" (default) or "csv"
     * @return ExportResult containing content bytes, content type, and filename
     * @throws IllegalArgumentException if type or format is invalid
     */
    public ExportResult export(String type, Map<String, Object> data, String format) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type must not be blank");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data must not be empty");
        }
        if (!type.equals("helloworld")
                && !type.equals("hash")
                && !type.equals("bubblesort")) {
            String msg = "Invalid type: " + type
                    + ". Must be one of: helloworld, hash, bubblesort";
            throw new IllegalArgumentException(msg);
        }
        if (format == null || format.isBlank()) {
            format = "json";
        }
        format = format.toLowerCase();
        if (!format.equals("json") && !format.equals("csv")) {
            throw new IllegalArgumentException("Invalid format: " + format + ". Must be json or csv");
        }

        String ext = format.equals("json") ? "json" : "csv";
        String timestamp = LocalDateTime.now(ZoneOffset.UTC).format(DATE_FORMAT);
        String filename = "export-" + type + "-" + timestamp + "." + ext;

        try {
            if ("json".equals(format)) {
                String json = objectMapper.writeValueAsString(data);
                byte[] content = json.getBytes(StandardCharsets.UTF_8);
                return new ExportResult(content, JSON_TYPE, filename);
            } else {
                StringBuilder csv = new StringBuilder();
                if ("bubblesort".equals(type)) {
                    // Convert steps to CSV
                    Object stepsObj = data.get("steps");
                    if (stepsObj instanceof List) {
                        csv.append("round,after,swapped\n");
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> steps = (List<Map<String, Object>>) stepsObj;
                        for (Map<String, Object> step : steps) {
                            csv.append(step.get("round")).append(",");
                            csv.append("\"").append(step.get("after")).append("\"").append(",");
                            csv.append(step.get("swapped")).append("\n");
                        }
                    }
                } else {
                    // helloworld or hash: key-value pairs
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        csv.append(entry.getKey()).append(",")
                           .append("\"").append(entry.getValue()).append("\"\n");
                    }
                }
                byte[] content = csv.toString().getBytes(StandardCharsets.UTF_8);
                return new ExportResult(content, CSV_TYPE, filename);
            }
        } catch (Exception e) {
            log.error("Export failed", e);
            throw new IllegalArgumentException("Export failed: " + e.getMessage(), e);
        }
    }
}
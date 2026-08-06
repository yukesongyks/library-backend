package com.library.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ExportService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] exportCsv(String type, Object data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));

        Map<String, Object> dataMap = toMap(data);

        switch (type) {
            case "hello" -> {
                writer.println("key,value");
                writer.println("message," + escapeCsv(dataMap.getOrDefault("message", "")));
            }
            case "hash" -> {
                writer.println("algorithm,digest");
                writer.println(escapeCsv(dataMap.getOrDefault("algorithm", "")) + ","
                        + escapeCsv(dataMap.getOrDefault("digest", "")));
            }
            case "bubble-sort" -> {
                writer.println("sorted,comparisons,swaps");
                Object sorted = dataMap.getOrDefault("sorted", Collections.emptyList());
                writer.println(escapeCsv(sorted) + ","
                        + dataMap.getOrDefault("comparisons", 0) + ","
                        + dataMap.getOrDefault("swaps", 0));
            }
            default -> throw new IllegalArgumentException("不支持的导出类型: " + type);
        }

        writer.flush();
        return baos.toByteArray();
    }

    public byte[] exportXlsx(String type, Object data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(type);
            Map<String, Object> dataMap = toMap(data);

            switch (type) {
                case "hello" -> {
                    Row header = sheet.createRow(0);
                    header.createCell(0).setCellValue("key");
                    header.createCell(1).setCellValue("value");
                    Row row = sheet.createRow(1);
                    row.createCell(0).setCellValue("message");
                    row.createCell(1).setCellValue(String.valueOf(dataMap.getOrDefault("message", "")));
                }
                case "hash" -> {
                    Row header = sheet.createRow(0);
                    header.createCell(0).setCellValue("algorithm");
                    header.createCell(1).setCellValue("digest");
                    Row row = sheet.createRow(1);
                    row.createCell(0).setCellValue(String.valueOf(dataMap.getOrDefault("algorithm", "")));
                    row.createCell(1).setCellValue(String.valueOf(dataMap.getOrDefault("digest", "")));
                }
                case "bubble-sort" -> {
                    Row header = sheet.createRow(0);
                    header.createCell(0).setCellValue("sorted");
                    header.createCell(1).setCellValue("comparisons");
                    header.createCell(2).setCellValue("swaps");
                    Row row = sheet.createRow(1);
                    Object sorted = dataMap.getOrDefault("sorted", Collections.emptyList());
                    row.createCell(0).setCellValue(String.valueOf(sorted));
                    row.createCell(1).setCellValue(toInt(dataMap.getOrDefault("comparisons", 0)));
                    row.createCell(2).setCellValue(toInt(dataMap.getOrDefault("swaps", 0)));
                }
                default -> throw new IllegalArgumentException("不支持的导出类型: " + type);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object data) {
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        return objectMapper.convertValue(data, Map.class);
    }

    private String escapeCsv(Object value) {
        String str = String.valueOf(value);
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}

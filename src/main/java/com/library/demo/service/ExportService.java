package com.library.demo.service;

import com.library.demo.model.entity.ApiCallLog;
import com.library.demo.repository.ApiCallLogRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private static final int MAX_EXPORT_RECORDS = 10000;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ApiCallLogRepository repository;

    public ExportService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    public byte[] export(String type, List<Long> recordIds) throws IOException {
        List<ApiCallLog> records;
        if (recordIds != null && !recordIds.isEmpty()) {
            records = repository.findAllById(recordIds);
        } else {
            records = repository.findByApiTypeOrderByCreatedAtDesc(type);
        }

        if (records.size() > MAX_EXPORT_RECORDS) {
            records = records.subList(0, MAX_EXPORT_RECORDS);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("API Call Records");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"序号", "调用时间", "调用人", "输入参数", "输出结果", "耗时(ms)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            for (int i = 0; i < records.size(); i++) {
                ApiCallLog log = records.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(log.getCreatedAt() != null ? log.getCreatedAt().format(FMT) : "");
                row.createCell(2).setCellValue(log.getUserName() != null ? log.getUserName() : "");
                row.createCell(3).setCellValue(log.getRequestPayload() != null ? log.getRequestPayload() : "");
                row.createCell(4).setCellValue(log.getResponsePayload() != null ? log.getResponsePayload() : "");
                row.createCell(5).setCellValue(log.getDurationMs() != null ? log.getDurationMs() : 0);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}

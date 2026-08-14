package com.library.service;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

@Service
public class ExportService {

    public byte[] export(String type, Map<String, Object> data, String format) {
        return "xlsx".equals(format) ? exportXlsx(type, data) : exportCsv(type, data);
    }

    private byte[] exportCsv(String type, Map<String, Object> data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(bos))) {
            writer.writeNext(new String[]{"Key", "Value"});
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String value = formatValue(entry.getValue());
                writer.writeNext(new String[]{entry.getKey(), value});
            }
            writer.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("CSV导出失败", e);
        }
    }

    private byte[] exportXlsx(String type, Map<String, Object> data) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(type);
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Key");
            header.createCell(1).setCellValue("Value");
            int rowIdx = 1;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(formatValue(entry.getValue()));
            }
            wb.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("XLSX导出失败", e);
        }
    }

    private String formatValue(Object value) {
        if (value == null) return "";
        if (value instanceof Object[]) return java.util.Arrays.toString((Object[]) value);
        return value.toString();
    }
}
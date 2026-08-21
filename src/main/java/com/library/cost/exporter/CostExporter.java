package com.library.cost.exporter;

import com.library.cost.dto.CostAnalysisItem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CostExporter {

    private static final String[] HEADERS = {"名称", "人力成本", "项目预算", "实际消耗", "预算占比", "预计超支金额"};

    public byte[] toXlsx(List<CostAnalysisItem> items) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("成本统计报表");
            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                header.createCell(i).setCellValue(HEADERS[i]);
            }
            int r = 1;
            for (CostAnalysisItem it : items) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(it.name());
                row.createCell(1).setCellValue(it.laborCost().doubleValue());
                row.createCell(2).setCellValue(it.projectBudget().doubleValue());
                row.createCell(3).setCellValue(it.projectActual().doubleValue());
                row.createCell(4).setCellValue(it.budgetRatio());
                row.createCell(5).setCellValue(it.overBudgetAmount().doubleValue());
            }
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] toCsv(List<CostAnalysisItem> items) {
        StringBuilder sb = new StringBuilder(String.join(",", HEADERS)).append("\r\n");
        for (CostAnalysisItem it : items) {
            sb.append(csv(it.name())).append(',')
              .append(it.laborCost()).append(',')
              .append(it.projectBudget()).append(',')
              .append(it.projectActual()).append(',')
              .append(String.format("%.2f", it.budgetRatio())).append(',')
              .append(it.overBudgetAmount()).append("\r\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String csv(String value) {
        if (value == null) {
            return "\"\"";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
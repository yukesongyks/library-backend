package com.library.backend.service;

import com.library.backend.dto.CostSummaryDTO;
import com.library.backend.dto.DimensionStatDTO;
import com.library.backend.dto.ProjectCostDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ExcelExportService {

    /**
     * 成本汇总 Excel 导出（S06）。
     * Sheet1 总览 + Sheet2 部门明细 + Sheet3 角色明细 + Sheet4 月份明细。
     */
    public byte[] exportCostSummary(CostSummaryDTO summary) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Sheet1 总览
            Sheet overviewSheet = workbook.createSheet("成本总览");
            createOverviewSheet(overviewSheet, summary);

            // Sheet2 部门明细
            Sheet deptSheet = workbook.createSheet("部门明细");
            createDimensionSheet(deptSheet, summary.getByDepartment(), "部门");

            // Sheet3 角色明细
            Sheet roleSheet = workbook.createSheet("角色明细");
            createDimensionSheet(roleSheet, summary.getByRole(), "角色");

            // Sheet4 月份明细
            Sheet monthSheet = workbook.createSheet("月份明细");
            createDimensionSheet(monthSheet, summary.getByMonth(), "月份");

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * 项目成本 Excel 导出（S07）。
     * Sheet1 项目列表（项目名/预算/实际/占比/超支）。
     */
    public byte[] exportProjectCost(List<ProjectCostDTO> projects) throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("项目成本");
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Header
            Row header = sheet.createRow(0);
            String[] headers = {"项目ID", "项目名称", "预算金额", "实际消耗", "预算占比(%)", "预计超支金额"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 1;
            for (ProjectCostDTO p : projects) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getProjectId());
                row.createCell(1).setCellValue(p.getProjectName());
                setNumericCell(row, 2, p.getBudgetAmount());
                setNumericCell(row, 3, p.getActualCost());
                setNumericCell(row, 4, p.getBudgetUsageRate());
                setNumericCell(row, 5, p.getOverspendAmount());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ===== 内部辅助方法 =====

    private void createOverviewSheet(Sheet sheet, CostSummaryDTO summary) {
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());

        Row header = sheet.createRow(0);
        String[] headers = {"指标", "金额(¥)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Row r1 = sheet.createRow(1);
        r1.createCell(0).setCellValue("总成本");
        setNumericCell(r1, 1, summary.getTotalCost());

        Row r2 = sheet.createRow(2);
        r2.createCell(0).setCellValue("人力成本");
        setNumericCell(r2, 1, summary.getLaborCost());

        Row r3 = sheet.createRow(3);
        r3.createCell(0).setCellValue("记录数");
        r3.createCell(1).setCellValue(summary.getRecordCount());

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDimensionSheet(Sheet sheet, List<DimensionStatDTO> stats, String dimLabel) {
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());

        Row header = sheet.createRow(0);
        String[] headers = {dimLabel, "金额(¥)", "占比(%)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (DimensionStatDTO stat : stats) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(stat.getDimensionName());
            setNumericCell(row, 1, stat.getAmount());
            row.createCell(2).setCellValue(stat.getPercentage());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void setNumericCell(Row row, int col, BigDecimal value) {
        if (value != null) {
            row.createCell(col).setCellValue(value.doubleValue());
        }
    }
}

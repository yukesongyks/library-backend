package com.library.export.service;

import com.library.common.enums.BizTypeEnum;
import com.library.common.exception.BizException;
import com.library.demo.model.DemoResult;
import com.library.demo.model.HashRequest;
import com.library.demo.model.HashResult;
import com.library.demo.model.SortRequest;
import com.library.demo.model.SortResult;
import com.library.demo.service.DemoService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * 导出服务（S04）
 * 降级兜底：Excel生成异常返回EXPORT_003（异常兜底方案 6.1.4）
 */
@Service
public class ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    @Autowired
    private DemoService demoService;

    /**
     * 导出指定业务类型的结果为 Excel
     */
    public byte[] export(String bizType, Map<String, String> params) {
        // R01: bizType 非空
        if (bizType == null || bizType.trim().isEmpty()) {
            throw new BizException("EXPORT_001", "bizType不能为空");
        }
        // R02: bizType 合法性
        BizTypeEnum bizEnum = BizTypeEnum.fromCode(bizType.trim());
        if (bizEnum == null) {
            throw new BizException("EXPORT_002", "不支持的bizType: " + bizType);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(bizEnum.getCode());

            switch (bizEnum) {
                case HELLOWORLD:
                    writeHelloworld(sheet);
                    break;
                case HASH:
                    writeHash(sheet, params);
                    break;
                case BUBBLE_SORT:
                    writeBubbleSort(sheet, params);
                    break;
                default:
                    throw new BizException("EXPORT_002", "不支持的bizType: " + bizType);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出文件生成异常: bizType={}", bizType, e);
            throw new BizException("EXPORT_003", "导出文件生成异常: " + e.getMessage());
        }
    }

    private void writeHelloworld(Sheet sheet) {
        DemoResult result = demoService.helloworld();
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("result");
        headerRow.createCell(1).setCellValue("timestamp");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(result.getResult());
        dataRow.createCell(1).setCellValue(result.getTimestamp());
    }

    private void writeHash(Sheet sheet, Map<String, String> params) {
        HashRequest req = new HashRequest();
        req.setText(params.get("text"));
        req.setAlgorithm(params.get("algorithm"));
        HashResult result = demoService.hash(req);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("algorithm");
        headerRow.createCell(1).setCellValue("input");
        headerRow.createCell(2).setCellValue("hash");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(result.getAlgorithm());
        dataRow.createCell(1).setCellValue(result.getInput());
        dataRow.createCell(2).setCellValue(result.getHash());
    }

    private void writeBubbleSort(Sheet sheet, Map<String, String> params) {
        SortRequest req = new SortRequest();
        String numbersStr = params.get("numbers");
        if (numbersStr != null && !numbersStr.trim().isEmpty()) {
            String[] parts = numbersStr.split(",");
            java.util.List<Integer> nums = new java.util.ArrayList<>();
            for (String p : parts) {
                nums.add(Integer.parseInt(p.trim()));
            }
            req.setNumbers(nums);
        }
        SortResult result = demoService.bubbleSort(req);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("index");
        headerRow.createCell(1).setCellValue("value");
        headerRow.createCell(2).setCellValue("costMs");
        headerRow.createCell(3).setCellValue("size");

        List<Integer> sorted = result.getSorted();
        for (int i = 0; i < sorted.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue(sorted.get(i));
            if (i == 0) {
                row.createCell(2).setCellValue(result.getCostMs());
                row.createCell(3).setCellValue(result.getSize());
            }
        }
    }
}

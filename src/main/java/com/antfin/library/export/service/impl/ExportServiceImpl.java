package com.antfin.library.export.service.impl;

import com.antfin.library.algorithm.model.vo.BubbleSortResultVO;
import com.antfin.library.algorithm.model.vo.HashResultVO;
import com.antfin.library.algorithm.model.vo.HelloWorldVO;
import com.antfin.library.algorithm.service.AlgorithmService;
import com.antfin.library.common.enums.AlgorithmTypeEnum;
import com.antfin.library.common.exception.BusinessException;
import com.antfin.library.common.util.UserContextUtil;
import com.antfin.library.export.service.ExportService;
import com.antfin.library.tracking.service.TrackService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出服务实现（Excel .xlsx 格式）
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportServiceImpl.class);

    private static final int MAX_ROWS = 10000;

    private final AlgorithmService algorithmService;

    private final TrackService trackService;

    public ExportServiceImpl(AlgorithmService algorithmService, TrackService trackService) {
        this.algorithmService = algorithmService;
        this.trackService = trackService;
    }

    @Override
    public void exportAlgorithmResult(String algorithmType, String numbers, String inputText,
                                       String algorithm, HttpServletResponse response) {
        AlgorithmTypeEnum typeEnum = AlgorithmTypeEnum.fromCode(algorithmType);
        if (typeEnum == null) {
            throw new BusinessException("PARAM_ERROR", "无效的算法类型: " + algorithmType);
        }

        String fileName = "algorithm-result-" + typeEnum.getCode().toLowerCase() + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("算法结果");

            switch (typeEnum) {
                case HELLO_WORLD:
                    HelloWorldVO hw = algorithmService.helloWorld();
                    buildKeyValueSheet(sheet, new String[][]{
                            {"message", hw.getMessage()}
                    });
                    fileName = "helloworld-result.xlsx";
                    break;

                case HASH:
                    String text = inputText != null ? inputText : "";
                    String algo = algorithm != null ? algorithm : "SHA-256";
                    HashResultVO hashResult = algorithmService.hash(text, algo);
                    buildKeyValueSheet(sheet, new String[][]{
                            {"hashHex", hashResult.getHashHex()},
                            {"inputLength", String.valueOf(hashResult.getInputLength())}
                    });
                    fileName = "hash-result.xlsx";
                    break;

                case BUBBLE_SORT:
                    List<Integer> numberList = parseNumbers(numbers);
                    BubbleSortResultVO sortResult = algorithmService.bubbleSort(numberList);
                    buildKeyValueSheet(sheet, new String[][]{
                            {"sortedArray", sortResult.getSortedArray().toString()},
                            {"compareCount", String.valueOf(sortResult.getCompareCount())},
                            {"swapCount", String.valueOf(sortResult.getSwapCount())},
                            {"durationMillis", String.valueOf(sortResult.getDurationMillis())}
                    });
                    fileName = "bubble-sort-result.xlsx";
                    break;

                default:
                    throw new BusinessException("PARAM_ERROR", "不支持的算法类型: " + algorithmType);
            }

            trackService.trackAlgorithmCall("EXPORT", getCurrentUserId());

            writeExcelResponse(response, fileName, workbook);
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new BusinessException("EXPORT_ERROR", "导出失败: " + e.getMessage());
        }
    }

    /**
     * 构建"字段,值"两列表格
     */
    private void buildKeyValueSheet(Sheet sheet, String[][] rows) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("字段");
        header.createCell(1).setCellValue("值");
        for (int i = 0; i < rows.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(rows[i][0]);
            row.createCell(1).setCellValue(rows[i][1]);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    /**
     * 解析逗号分隔的数字数组
     */
    private List<Integer> parseNumbers(String numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new BusinessException("PARAM_ERROR", "冒泡排序数字数组不能为空");
        }
        String[] parts = numbers.split(",");
        if (parts.length > MAX_ROWS) {
            throw new BusinessException("PARAM_ERROR", "数组长度不能超过" + MAX_ROWS);
        }
        try {
            return Arrays.stream(parts)
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            // G16.2: 捕获异常后先记录日志再抛出
            log.warn("数字格式错误: {}", e.getMessage());
            throw new BusinessException("PARAM_ERROR", "数字格式错误: " + e.getMessage());
        }
    }

    /**
     * 写出 Excel 响应
     */
    private void writeExcelResponse(HttpServletResponse response, String fileName, Workbook workbook)
            throws IOException {
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName);
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        }
    }

    private String getCurrentUserId() {
        return UserContextUtil.getCurrentUserId();
    }
}

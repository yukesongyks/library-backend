package com.antfin.library.export.service.impl;

import com.antfin.library.algorithm.model.vo.BubbleSortResultVO;
import com.antfin.library.algorithm.model.vo.HashResultVO;
import com.antfin.library.algorithm.model.vo.HelloWorldVO;
import com.antfin.library.algorithm.service.AlgorithmService;
import com.antfin.library.common.enums.AlgorithmTypeEnum;
import com.antfin.library.common.exception.BusinessException;
import com.antfin.library.export.service.ExportService;
import com.antfin.library.tracking.service.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出服务实现
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

        String fileName = "algorithm-result-" + typeEnum.getCode().toLowerCase() + ".csv";
        StringBuilder csv = new StringBuilder();

        switch (typeEnum) {
            case HELLO_WORLD:
                HelloWorldVO hw = algorithmService.helloWorld();
                csv.append("字段,值\n");
                csv.append("message,").append(escapeCsv(hw.getMessage())).append("\n");
                fileName = "helloworld-result.csv";
                break;

            case HASH:
                String text = inputText != null ? inputText : "";
                String algo = algorithm != null ? algorithm : "SHA-256";
                HashResultVO hashResult = algorithmService.hash(text, algo);
                csv.append("字段,值\n");
                csv.append("hashHex,").append(escapeCsv(hashResult.getHashHex())).append("\n");
                csv.append("inputLength,").append(hashResult.getInputLength()).append("\n");
                fileName = "hash-result.csv";
                break;

            case BUBBLE_SORT:
                List<Integer> numberList = parseNumbers(numbers);
                BubbleSortResultVO sortResult = algorithmService.bubbleSort(numberList);
                csv.append("字段,值\n");
                csv.append("sortedArray,").append(escapeCsv(sortResult.getSortedArray().toString())).append("\n");
                csv.append("compareCount,").append(sortResult.getCompareCount()).append("\n");
                csv.append("swapCount,").append(sortResult.getSwapCount()).append("\n");
                csv.append("durationMillis,").append(sortResult.getDurationMillis()).append("\n");
                fileName = "bubble-sort-result.csv";
                break;

            default:
                throw new BusinessException("PARAM_ERROR", "不支持的算法类型: " + algorithmType);
        }

        trackService.trackAlgorithmCall("EXPORT", getCurrentUserId());

        writeCsvResponse(response, fileName, csv.toString());
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
            throw new BusinessException("PARAM_ERROR", "数字格式错误: " + e.getMessage());
        }
    }

    /**
     * CSV 字段转义
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * 写出 CSV 响应
     */
    private void writeCsvResponse(HttpServletResponse response, String fileName, String content) {
        response.setContentType("text/csv;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName);

            // 写入 BOM 以兼容 Excel
            PrintWriter writer = response.getWriter();
            writer.write("\uFEFF");
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            log.error("导出CSV失败", e);
            throw new BusinessException("EXPORT_ERROR", "导出失败: " + e.getMessage());
        }
    }

    private String getCurrentUserId() {
        return "demo-user";
    }
}

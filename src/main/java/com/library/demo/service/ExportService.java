package com.library.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo.dto.request.ExportRequest;
import com.library.demo.entity.DemoCallLog;
import com.library.demo.enums.ApiType;
import com.library.demo.mapper.DemoCallLogMapper;
import com.library.demo.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final DemoCallLogMapper callLogMapper;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] export(ExportRequest request) throws IOException {
        ApiType apiType = ApiType.valueOf(request.getType());

        LambdaQueryWrapper<DemoCallLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DemoCallLog::getApiType, apiType.name());
        wrapper.orderByDesc(DemoCallLog::getCallTime);
        wrapper.last("LIMIT 10000");

        if (request.getRecordIds() != null && !request.getRecordIds().isEmpty()) {
            wrapper.in(DemoCallLog::getId, request.getRecordIds());
        }

        List<DemoCallLog> logs = callLogMapper.selectList(wrapper);

        String[] headers;
        List<List<String>> rows = new ArrayList<>();

        switch (apiType) {
            case HELLOWORLD:
                headers = new String[]{"序号", "输入名称", "返回结果", "调用时间", "耗时(ms)"};
                for (int i = 0; i < logs.size(); i++) {
                    DemoCallLog logEntry = logs.get(i);
                    List<String> row = new ArrayList<>();
                    row.add(String.valueOf(i + 1));
                    row.add(parseHelloWorldName(logEntry.getRequestParams()));
                    row.add(parseHelloWorldResult(logEntry.getResponseData()));
                    row.add(logEntry.getCallTime() != null ? logEntry.getCallTime().format(FMT) : "");
                    row.add(logEntry.getExecutionTimeMs() != null ? String.valueOf(logEntry.getExecutionTimeMs()) : "0");
                    rows.add(row);
                }
                break;
            case HASH:
                headers = new String[]{"序号", "原始文本", "算法类型", "哈希结果", "调用时间", "耗时(ms)"};
                for (int i = 0; i < logs.size(); i++) {
                    DemoCallLog logEntry = logs.get(i);
                    List<String> row = new ArrayList<>();
                    row.add(String.valueOf(i + 1));
                    row.add(parseHashInput(logEntry.getRequestParams()));
                    row.add(parseHashAlgorithm(logEntry.getRequestParams()));
                    row.add(parseHashResult(logEntry.getResponseData()));
                    row.add(logEntry.getCallTime() != null ? logEntry.getCallTime().format(FMT) : "");
                    row.add(logEntry.getExecutionTimeMs() != null ? String.valueOf(logEntry.getExecutionTimeMs()) : "0");
                    rows.add(row);
                }
                break;
            case BUBBLE_SORT:
                headers = new String[]{"序号", "原始数组", "排序结果", "排序方向", "交换次数", "调用时间", "耗时(ms)"};
                for (int i = 0; i < logs.size(); i++) {
                    DemoCallLog logEntry = logs.get(i);
                    List<String> row = new ArrayList<>();
                    row.add(String.valueOf(i + 1));
                    row.add(parseBubbleSortOriginal(logEntry.getRequestParams()));
                    row.add(parseBubbleSortSorted(logEntry.getResponseData()));
                    row.add(parseBubbleSortOrder(logEntry.getResponseData()));
                    row.add(parseBubbleSortSwapCount(logEntry.getResponseData()));
                    row.add(logEntry.getCallTime() != null ? logEntry.getCallTime().format(FMT) : "");
                    row.add(logEntry.getExecutionTimeMs() != null ? String.valueOf(logEntry.getExecutionTimeMs()) : "0");
                    rows.add(row);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown api type: " + apiType);
        }

        return ExcelUtil.generateExcel(apiType.name() + "_export", headers, rows);
    }

    // === HelloWorld JSON 解析 ===

    private String parseHelloWorldName(String requestParams) {
        return parseJsonField(requestParams, "name", "");
    }

    private String parseHelloWorldResult(String responseData) {
        return parseJsonField(responseData, "result", "");
    }

    // === Hash JSON 解析 ===

    private String parseHashInput(String requestParams) {
        return parseJsonField(requestParams, "input", "");
    }

    private String parseHashAlgorithm(String requestParams) {
        return parseJsonField(requestParams, "algorithm", "SHA256");
    }

    private String parseHashResult(String responseData) {
        return parseJsonField(responseData, "hashResult", "");
    }

    // === BubbleSort JSON 解析 ===

    private String parseBubbleSortOriginal(String requestParams) {
        return parseJsonArrayField(requestParams, "numbers", "[]");
    }

    private String parseBubbleSortSorted(String responseData) {
        return parseJsonArrayField(responseData, "sorted", "[]");
    }

    private String parseBubbleSortOrder(String responseData) {
        return parseJsonField(responseData, "order", "");
    }

    private String parseBubbleSortSwapCount(String responseData) {
        return parseJsonField(responseData, "swapCount", "0");
    }

    // === 通用 JSON 解析工具 ===

    private String parseJsonField(String json, String fieldName, String defaultValue) {
        if (json == null || json.isBlank()) {
            return defaultValue;
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            // 请求参数被序列化为数组 [actualObject]，取第一个元素
            if (node.isArray() && node.size() > 0) {
                node = node.get(0);
            }
            JsonNode field = node.get(fieldName);
            if (field == null || field.isNull()) {
                return defaultValue;
            }
            return field.isTextual() ? field.asText() : field.toString();
        } catch (Exception e) {
            log.warn("Failed to parse JSON field '{}' from: {}", fieldName, json, e);
            return defaultValue;
        }
    }

    private String parseJsonArrayField(String json, String fieldName, String defaultValue) {
        if (json == null || json.isBlank()) {
            return defaultValue;
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node.isArray() && node.size() > 0) {
                node = node.get(0);
            }
            JsonNode field = node.get(fieldName);
            if (field == null || field.isNull()) {
                return defaultValue;
            }
            return field.toString();
        } catch (Exception e) {
            log.warn("Failed to parse JSON array field '{}' from: {}", fieldName, json, e);
            return defaultValue;
        }
    }
}

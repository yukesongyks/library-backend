package com.example.library.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 导出服务：返回各 Tab 的样本数据（幂等），支持 CSV 与 JSON 格式。
 * 对应 design.md 2.3 / tasks E1。
 */
@Service
public class ExportService {

    private static final List<String> VALID_TABS =
            Arrays.asList("helloworld", "hash", "bubble-sort");
    private static final List<String> VALID_FORMATS =
            Arrays.asList("csv", "json");

    public List<Map<String, Object>> exportData(String tab) {
        validateTab(tab);
        List<Map<String, Object>> data = new ArrayList<>();
        switch (tab) {
            case "helloworld":
                Map<String, Object> hw = new LinkedHashMap<>();
                hw.put("result", "Hello, World!");
                data.add(hw);
                break;
            case "hash":
                Map<String, Object> hashSample = new LinkedHashMap<>();
                hashSample.put("algorithm", "SHA-256");
                hashSample.put("input", "abc");
                hashSample.put("hash", "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
                data.add(hashSample);
                break;
            case "bubble-sort":
                Map<String, Object> sortSample = new LinkedHashMap<>();
                sortSample.put("input", Arrays.asList(3, 1, 2));
                sortSample.put("sorted", Arrays.asList(1, 2, 3));
                sortSample.put("steps", 2);
                data.add(sortSample);
                break;
            default:
                throw new IllegalArgumentException("不支持的 tab: " + tab);
        }
        return data;
    }

    public String toCsv(List<Map<String, Object>> data) {
        if (data.isEmpty()) {
            return "";
        }
        List<String> headers = new ArrayList<>(data.get(0).keySet());
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append("\n");
        for (Map<String, Object> row : data) {
            List<String> values = new ArrayList<>();
            for (String header : headers) {
                Object val = row.get(header);
                String cell = val == null ? "" : val.toString().replace("\"", "\"\"");
                values.add("\"" + cell + "\"");
            }
            sb.append(String.join(",", values)).append("\n");
        }
        return sb.toString();
    }

    public void validateTab(String tab) {
        if (!VALID_TABS.contains(tab)) {
            throw new IllegalArgumentException("不支持的 tab: " + tab + "，支持: " + VALID_TABS);
        }
    }

    public void validateFormat(String format) {
        if (!VALID_FORMATS.contains(format)) {
            throw new IllegalArgumentException("不支持的 format: " + format + "，支持: " + VALID_FORMATS);
        }
    }
}

package com.library.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.demo.dto.request.ExportRequest;
import com.library.demo.entity.DemoCallLog;
import com.library.demo.enums.ApiType;
import com.library.demo.mapper.DemoCallLogMapper;
import com.library.demo.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final DemoCallLogMapper callLogMapper;
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
                break;
            case HASH:
                headers = new String[]{"序号", "原始文本", "算法类型", "哈希结果", "调用时间", "耗时(ms)"};
                break;
            case BUBBLE_SORT:
                headers = new String[]{"序号", "原始数组", "排序结果", "排序方向", "交换次数", "调用时间", "耗时(ms)"};
                break;
            default:
                throw new IllegalArgumentException("Unknown api type: " + apiType);
        }

        for (int i = 0; i < logs.size(); i++) {
            DemoCallLog log = logs.get(i);
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(i + 1));
            row.add(log.getRequestParams() != null ? log.getRequestParams() : "");
            row.add(log.getResponseData() != null ? log.getResponseData() : "");
            row.add(log.getCallTime() != null ? log.getCallTime().format(FMT) : "");
            row.add(log.getExecutionTimeMs() != null ? String.valueOf(log.getExecutionTimeMs()) : "0");

            if (apiType == ApiType.BUBBLE_SORT) {
                row.add(2, ""); // 排序结果占位
                row.add(3, ""); // 排序方向占位
                row.add(4, "0"); // 交换次数占位
            }

            rows.add(row);
        }

        return ExcelUtil.generateExcel(apiType.name() + "_export", headers, rows);
    }
}

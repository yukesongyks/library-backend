package com.library.demo.service;

import com.library.common.enums.HashAlgorithmEnum;
import com.library.common.exception.BizException;
import com.library.demo.model.DemoResult;
import com.library.demo.model.HashRequest;
import com.library.demo.model.HashResult;
import com.library.demo.model.SortRequest;
import com.library.demo.model.SortResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * demo 演示服务（S01/S02/S03）
 */
@Service
public class DemoService {

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * helloworld 服务
     */
    public DemoResult helloworld() {
        DemoResult result = new DemoResult();
        result.setResult("Hello, World!");
        result.setTimestamp(LocalDateTime.now().format(TS_FMT));
        return result;
    }

    /**
     * 哈希计算服务
     */
    public HashResult hash(HashRequest req) {
        // R01: text 不能为空
        if (req.getText() == null || req.getText().trim().isEmpty()) {
            throw new BizException("DEMO_002", "输入文本不能为空");
        }
        // R02: 算法合法性
        String algoParam = req.getAlgorithm();
        HashAlgorithmEnum algoEnum;
        if (algoParam == null || algoParam.trim().isEmpty()) {
            algoEnum = HashAlgorithmEnum.SHA_256; // 默认
        } else {
            algoEnum = HashAlgorithmEnum.fromCode(algoParam.trim());
            if (algoEnum == null) {
                throw new BizException("DEMO_003", "不支持的哈希算法: " + algoParam);
            }
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algoEnum.getCode());
            byte[] digest = md.digest(req.getText().getBytes(StandardCharsets.UTF_8));
            String hex = bytesToHex(digest);

            HashResult result = new HashResult();
            result.setAlgorithm(algoEnum.name());
            result.setInput(req.getText());
            result.setHash(hex);
            return result;
        } catch (Exception e) {
            throw new BizException("DEMO_001", "哈希计算异常: " + e.getMessage());
        }
    }

    /**
     * 冒泡排序服务
     */
    public SortResult bubbleSort(SortRequest req) {
        // R01: 数组不能为空
        if (req.getNumbers() == null || req.getNumbers().isEmpty()) {
            throw new BizException("DEMO_004", "数组不能为空");
        }
        // R02: 长度限制
        if (req.getNumbers().size() > 1000) {
            throw new BizException("DEMO_005", "数组长度超过限制（最大1000）");
        }

        long start = System.currentTimeMillis();
        // 执行冒泡排序（升序）
        List<Integer> sorted = new ArrayList<>(req.getNumbers());
        bubbleSortAscending(sorted);
        long costMs = System.currentTimeMillis() - start;

        SortResult result = new SortResult();
        result.setSorted(sorted);
        result.setCostMs(costMs);
        result.setSize(sorted.size());
        return result;
    }

    /**
     * 冒泡排序（升序）
     */
    private void bubbleSortAscending(List<Integer> arr) {
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    Collections.swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // 已有序，提前退出
            }
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

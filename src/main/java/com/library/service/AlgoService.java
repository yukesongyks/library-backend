package com.library.service;

import com.library.dto.AlgoResult;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class AlgoService {

    public AlgoResult helloworld() {
        long start = System.currentTimeMillis();
        String output = "Hello, World!";
        long duration = System.currentTimeMillis() - start;
        return new AlgoResult("helloworld", null, output, duration);
    }

    public AlgoResult hash(String input) {
        long start = System.currentTimeMillis();
        String output = sha256(input);
        long duration = System.currentTimeMillis() - start;
        return new AlgoResult("hash", input, output, duration);
    }

    public AlgoResult bubblesort(String input) {
        long start = System.currentTimeMillis();
        List<Integer> arr = parseInput(input);
        List<Integer> sorted = bubbleSort(arr);
        long duration = System.currentTimeMillis() - start;
        return new AlgoResult("bubblesort", arr, sorted, duration);
    }

    /**
     * B1 修复：NoSuchAlgorithmException 对 SHA-256 不会发生（JRE 标准算法），
     * 用 IllegalStateException 表示"不应发生"而非 RuntimeException。
     */
    private String sha256(String base) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // P3-1: 显式指定 UTF-8 字符集，保证跨环境哈希结果一致
            byte[] digest = md.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 算法不可用，JRE 环境异常", e);
        }
    }

    /**
     * B2 修复：非法输入抛出 IllegalArgumentException（Spring 自动映射为 400），
     * 过滤空字符串元素。
     */
    private List<Integer> parseInput(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("输入不能为空");
        }
        String[] parts = input.split(",");
        List<Integer> arr = new ArrayList<>();
        for (String p : parts) {
            String trimmed = p.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                arr.add(Integer.parseInt(trimmed));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("非法数字: \"" + trimmed + "\"，请输入逗号分隔的整数");
            }
        }
        if (arr.isEmpty()) {
            throw new IllegalArgumentException("未解析到有效数字");
        }
        return arr;
    }

    private List<Integer> bubbleSort(List<Integer> arr) {
        List<Integer> a = new ArrayList<>(arr);
        int n = a.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (a.get(j) > a.get(j + 1)) {
                    int tmp = a.get(j);
                    a.set(j, a.get(j + 1));
                    a.set(j + 1, tmp);
                }
            }
        }
        return a;
    }
}

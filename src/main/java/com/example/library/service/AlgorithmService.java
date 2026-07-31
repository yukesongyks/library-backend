package com.example.library.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 业务服务：哈希计算 + 冒泡排序。
 * 对应 tasks C2/C3 业务逻辑。
 */
@Service
public class AlgorithmService {

    private static final List<String> SUPPORTED_ALGORITHMS = Arrays.asList("SHA-256", "SHA-512", "MD5");

    /**
     * 计算哈希。不支持算法抛 IllegalArgumentException（全局异常处理为 400）。
     */
    public String hash(String algorithm, String input) {
        if (!SUPPORTED_ALGORITHMS.contains(algorithm)) {
            throw new IllegalArgumentException("不支持的算法: " + algorithm + "，支持: " + SUPPORTED_ALGORITHMS);
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("算法不可用: " + algorithm);
        }
    }

    /**
     * 冒泡排序，返回排序结果与交换步数。
     */
    public BubbleSortResult bubbleSort(List<Integer> numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("numbers 不能为空");
        }
        List<Integer> arr = new ArrayList<>(numbers);
        int steps = 0;
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    int tmp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, tmp);
                    steps++;
                }
            }
        }
        return new BubbleSortResult(numbers, arr, steps);
    }

    public static class BubbleSortResult {
        private final List<Integer> input;
        private final List<Integer> sorted;
        private final int steps;

        public BubbleSortResult(List<Integer> input, List<Integer> sorted, int steps) {
            this.input = input;
            this.sorted = sorted;
            this.steps = steps;
        }

        public List<Integer> getInput() {
            return input;
        }

        public List<Integer> getSorted() {
            return sorted;
        }

        public int getSteps() {
            return steps;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

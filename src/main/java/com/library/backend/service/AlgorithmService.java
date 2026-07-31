package com.library.backend.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 算法服务。
 *
 * <p>实现三个算法接口的核心逻辑（spec {@code algorithms.md}）：
 * <ul>
 *   <li>{@link #helloworld()} 返回固定字符串 {@code "Hello, World!"}</li>
 *   <li>{@link #hash(String, String)} 支持 SHA-256 / MD5，不支持的算法抛
 *       {@code IllegalArgumentException("Unsupported algorithm: <name>")} → 400</li>
 *   <li>{@link #bubbleSort(List)} 冒泡排序，空数组返回空列表（合法）</li>
 * </ul>
 * 哈希结果以小写 hex 输出，与 spec 示例值一致。</p>
 */
@Service
public class AlgorithmService {

    /** 哈希接口支持的算法名称（spec: SHA-256 / MD5） */
    private static final Set<String> SUPPORTED_ALGORITHMS = Set.of("SHA-256", "MD5");

    public String helloworld() {
        return "Hello, World!";
    }

    /**
     * 计算文本哈希。
     *
     * @param text      待哈希文本，由 Controller {@code @Valid} 保证非空
     * @param algorithm 算法名（SHA-256 / MD5）
     * @return 小写 hex 哈希值
     * @throws IllegalArgumentException 不支持的算法 → 触发 HTTP 400
     * @throws NoSuchAlgorithmException 理论兜底：java.security 层未找到算法 → 500
     */
    public String hash(String text, String algorithm) {
        if (!SUPPORTED_ALGORITHMS.contains(algorithm)) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            // 理论不可达：SUPPORTED_ALGORITHMS 已校验，保留兜底
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm, e);
        }
    }

    /**
     * 冒泡排序（原地副本，不修改入参）。
     *
     * @param numbers 待排序整数列表，可为空
     * @return 升序排列的新列表
     */
    public List<Integer> bubbleSort(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> sorted = new ArrayList<>(numbers);
        int n = sorted.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (sorted.get(j) > sorted.get(j + 1)) {
                    Integer tmp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, tmp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return sorted;
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

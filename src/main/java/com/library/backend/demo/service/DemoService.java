package com.library.backend.demo.service;

import com.library.backend.common.BizException;
import com.library.backend.common.ResultCode;
import com.library.backend.demo.constant.DemoConstants;
import com.library.backend.demo.dto.BubbleSortResponse;
import com.library.backend.demo.dto.HashResponse;
import com.library.backend.demo.enums.HashAlgorithmEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Demo 业务逻辑层：哈希计算、冒泡排序
 */
@Service
public class DemoService {

    private static final Logger log = LoggerFactory.getLogger(DemoService.class);

    /**
     * 返回 hello world
     *
     * @return "hello world"
     */
    public String helloWorld() {
        return DemoConstants.HELLO_WORLD;
    }

    /**
     * 计算字符串哈希值
     *
     * @param input     原始字符串
     * @param algorithm 算法名称（SHA256/MD5），缺省 SHA256
     * @return 哈希结果
     */
    public HashResponse hash(String input, String algorithm) {
        // 入参校验
        if (input == null || input.trim().isEmpty()) {
            throw new BizException(ResultCode.DEMO_0002);
        }
        if (input.length() > DemoConstants.MAX_INPUT_LENGTH) {
            throw new BizException(ResultCode.DEMO_0003);
        }

        // 算法解析（大小写不敏感）
        HashAlgorithmEnum algoEnum = HashAlgorithmEnum.fromString(algorithm);
        if (algoEnum == null) {
            throw new BizException(ResultCode.DEMO_0004);
        }

        String hashValue = computeHash(input, algoEnum);
        log.info("哈希计算完成: input={}, algorithm={}", input, algoEnum.name());

        return new HashResponse(input, algoEnum.name(), hashValue);
    }

    /**
     * 冒泡排序（升序）
     *
     * @param numbers 待排序数组
     * @return 原始数组 + 排序后数组
     */
    public BubbleSortResponse bubbleSort(List<Integer> numbers) {
        // 入参校验
        if (numbers == null || numbers.isEmpty()) {
            throw new BizException(ResultCode.DEMO_0005);
        }
        if (numbers.size() > DemoConstants.MAX_ARRAY_SIZE) {
            throw new BizException(ResultCode.DEMO_0006);
        }
        for (Integer num : numbers) {
            if (num == null || Math.abs(num) > DemoConstants.MAX_ELEMENT_VALUE) {
                throw new BizException(ResultCode.DEMO_0007);
            }
        }

        // 复制原始数组（不修改入参）
        List<Integer> original = new ArrayList<>(numbers);
        List<Integer> sorted = bubbleSortInternal(numbers);

        log.info("冒泡排序完成: size={}", sorted.size());
        return new BubbleSortResponse(original, sorted);
    }

    /**
     * 计算哈希值（十六进制小写）
     */
    private String computeHash(String input, HashAlgorithmEnum algorithm) {
        try {
            String algoName = algorithm == HashAlgorithmEnum.MD5 ? "MD5" : "SHA-256";
            MessageDigest digest = MessageDigest.getInstance(algoName);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("不支持的哈希算法: {}", algorithm, e);
            throw new BizException(ResultCode.DEMO_0004);
        }
    }

    /**
     * 字节数组转十六进制小写字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 优化冒泡排序（提前终止），升序
     */
    private List<Integer> bubbleSortInternal(List<Integer> numbers) {
        List<Integer> arr = new ArrayList<>(numbers);
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
                break;
            }
        }
        return arr;
    }
}

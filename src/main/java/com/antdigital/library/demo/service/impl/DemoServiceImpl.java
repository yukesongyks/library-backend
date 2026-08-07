package com.antdigital.library.demo.service.impl;

import com.antdigital.library.common.exception.ErrorCodeEnum;
import com.antdigital.library.common.exception.ServiceException;
import com.antdigital.library.demo.model.vo.BubbleSortVO;
import com.antdigital.library.demo.model.vo.HashVO;
import com.antdigital.library.demo.model.vo.HelloWorldVO;
import com.antdigital.library.demo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 演示功能服务实现。
 *
 * @author library-backend
 */
@Service
public class DemoServiceImpl implements DemoService {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    private static final String HASH_ALGORITHM = "SHA-256";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public HelloWorldVO helloWorld() {
        logger.info("helloWorld 方法被调用");
        String timestamp = LocalDateTime.now().format(FORMATTER);
        return new HelloWorldVO("Hello, World! Welcome to Library Backend.", timestamp);
    }

    @Override
    public HashVO hash(String input) {
        if (input == null || input.isEmpty()) {
            logger.warn("hash 参数为空");
            throw new ServiceException(ErrorCodeEnum.PARAM_EMPTY, "输入内容不能为空");
        }
        logger.info("hash 方法被调用, input: {}", input);
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String hashValue = bytesToHex(hashBytes);
            return new HashVO(input, HASH_ALGORITHM, hashValue);
        } catch (NoSuchAlgorithmException e) {
            logger.error("哈希算法不可用, algorithm: {}", HASH_ALGORITHM, e);
            throw new ServiceException(ErrorCodeEnum.COMPUTE_ERROR, "哈希算法不可用", "系统暂不支持该算法");
        }
    }

    @Override
    public BubbleSortVO bubbleSort(String input) {
        if (input == null || input.isBlank()) {
            logger.warn("bubbleSort 参数为空");
            throw new ServiceException(ErrorCodeEnum.PARAM_EMPTY, "排序内容不能为空");
        }
        logger.info("bubbleSort 方法被调用, input: {}", input);

        String[] parts = input.split(",");
        List<Integer> numbers = new ArrayList<>(parts.length);
        try {
            for (String part : parts) {
                numbers.add(Integer.parseInt(part.trim()));
            }
        } catch (NumberFormatException e) {
            logger.warn("输入包含非数字元素, input: {}", input);
            throw new ServiceException(ErrorCodeEnum.PARAM_INVALID, "输入包含非数字元素", "请输入逗号分隔的数字");
        }

        long startTime = System.currentTimeMillis();
        List<Integer> sorted = bubbleSortInternal(numbers);
        long costMs = System.currentTimeMillis() - startTime;

        return new BubbleSortVO(numbers, sorted, sorted.size(), costMs);
    }

    /**
     * 冒泡排序内部实现（升序）。
     *
     * @param arr 待排序数组
     * @return 排序后的新列表
     */
    private List<Integer> bubbleSortInternal(List<Integer> arr) {
        Integer[] array = arr.toArray(new Integer[0]);
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return Arrays.asList(array);
    }

    /**
     * 字节数组转十六进制字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

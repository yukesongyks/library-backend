package com.antfin.library.algorithm.service.impl;

import com.antfin.library.algorithm.model.vo.BubbleSortResultVO;
import com.antfin.library.algorithm.model.vo.HashResultVO;
import com.antfin.library.algorithm.model.vo.HelloWorldVO;
import com.antfin.library.algorithm.service.AlgorithmService;
import com.antfin.library.common.enums.HashAlgorithmEnum;
import com.antfin.library.tracking.service.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * 算法服务实现
 */
@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmServiceImpl.class);

    private static final String HELLO_WORLD_MESSAGE = "Hello, World! Welcome to Library Algorithm Demo.";

    private final TrackService trackService;

    public AlgorithmServiceImpl(TrackService trackService) {
        this.trackService = trackService;
    }

    @Override
    public HelloWorldVO helloWorld() {
        trackService.trackAlgorithmCall("HELLO_WORLD", getCurrentUserId());
        return new HelloWorldVO(HELLO_WORLD_MESSAGE);
    }

    @Override
    public HashResultVO hash(String inputText, String algorithm) {
        trackService.trackAlgorithmCall("HASH", getCurrentUserId());

        HashAlgorithmEnum hashAlgo = HashAlgorithmEnum.fromCodeOrDefault(algorithm);
        MessageDigest digest = hashAlgo.newMessageDigest();

        byte[] inputBytes = inputText.getBytes(StandardCharsets.UTF_8);
        byte[] hashBytes = digest.digest(inputBytes);

        String hashHex = bytesToHex(hashBytes);
        log.info("哈希计算完成: algorithm={}, inputLength={}", hashAlgo.getCode(), inputText.length());

        return new HashResultVO(hashHex, inputText.length());
    }

    @Override
    public BubbleSortResultVO bubbleSort(List<Integer> numbers) {
        trackService.trackAlgorithmCall("BUBBLE_SORT", getCurrentUserId());

        List<Integer> arr = new ArrayList<>(numbers);
        long compareCount = 0;
        long swapCount = 0;

        long startTime = System.nanoTime();

        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                compareCount++;
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapCount++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }

        long durationMillis = (System.nanoTime() - startTime) / 1_000_000;

        return new BubbleSortResultVO(arr, compareCount, swapCount, durationMillis);
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

    /**
     * 获取当前用户ID（简化实现，实际从上下文获取）
     */
    private String getCurrentUserId() {
        // 简化实现：从请求头或上下文获取，此处使用默认值
        // 实际项目中应从 SecurityContext 或 Session 获取
        return "demo-user";
    }
}

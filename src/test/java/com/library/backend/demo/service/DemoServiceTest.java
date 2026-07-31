package com.library.backend.demo.service;

import com.library.backend.common.BizException;
import com.library.backend.common.ResultCode;
import com.library.backend.demo.dto.BubbleSortResponse;
import com.library.backend.demo.dto.HashResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * DemoService 单元测试
 */
class DemoServiceTest {

    private DemoService demoService;

    @BeforeEach
    void setUp() {
        demoService = new DemoService();
    }

    // ========== HelloWorld 测试 ==========

    @Test
    @DisplayName("helloWorld - 返回 hello world 字符串")
    void helloWorld_shouldReturnHelloWorld() {
        String result = demoService.helloWorld();
        assertEquals("hello world", result);
    }

    // ========== 哈希算法测试 ==========

    @Test
    @DisplayName("hash - SHA256 默认算法")
    void hash_sha256Default() {
        HashResponse response = demoService.hash("hello", null);
        assertEquals("hello", response.getInput());
        assertEquals("SHA256", response.getAlgorithm());
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", response.getHashValue());
    }

    @Test
    @DisplayName("hash - SHA256 显式指定")
    void hash_sha256Explicit() {
        HashResponse response = demoService.hash("hello", "SHA256");
        assertEquals("SHA256", response.getAlgorithm());
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", response.getHashValue());
    }

    @Test
    @DisplayName("hash - MD5 算法")
    void hash_md5() {
        HashResponse response = demoService.hash("hello", "MD5");
        assertEquals("MD5", response.getAlgorithm());
        assertEquals("5d41402abc4b2a76b9719d911017c592", response.getHashValue());
    }

    @Test
    @DisplayName("hash - 算法大小写不敏感")
    void hash_caseInsensitive() {
        HashResponse response = demoService.hash("hello", "sha256");
        assertEquals("SHA256", response.getAlgorithm());
    }

    @Test
    @DisplayName("hash - input 为空抛 DEMO_0002")
    void hash_emptyInput() {
        BizException e = assertThrows(BizException.class, () -> demoService.hash("", null));
        assertEquals(ResultCode.DEMO_0002, e.getResultCode());
    }

    @Test
    @DisplayName("hash - input 为 null 抛 DEMO_0002")
    void hash_nullInput() {
        BizException e = assertThrows(BizException.class, () -> demoService.hash(null, null));
        assertEquals(ResultCode.DEMO_0002, e.getResultCode());
    }

    @Test
    @DisplayName("hash - 不支持的算法抛 DEMO_0004")
    void hash_unsupportedAlgorithm() {
        BizException e = assertThrows(BizException.class, () -> demoService.hash("hello", "RC4"));
        assertEquals(ResultCode.DEMO_0004, e.getResultCode());
    }

    // ========== 冒泡排序测试 ==========

    @Test
    @DisplayName("bubbleSort - 正常数组升序排序")
    void bubbleSort_normal() {
        List<Integer> numbers = Arrays.asList(5, 3, 8, 1, 9, 2);
        BubbleSortResponse response = demoService.bubbleSort(numbers);

        assertEquals(numbers, response.getOriginal());
        assertEquals(Arrays.asList(1, 2, 3, 5, 8, 9), response.getSorted());
    }

    @Test
    @DisplayName("bubbleSort - 已排序数组保持不变")
    void bubbleSort_alreadySorted() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        BubbleSortResponse response = demoService.bubbleSort(numbers);
        assertEquals(Arrays.asList(1, 2, 3), response.getSorted());
    }

    @Test
    @DisplayName("bubbleSort - 单元素数组")
    void bubbleSort_singleElement() {
        List<Integer> numbers = Collections.singletonList(42);
        BubbleSortResponse response = demoService.bubbleSort(numbers);
        assertEquals(numbers, response.getSorted());
    }

    @Test
    @DisplayName("bubbleSort - 空数组抛 DEMO_0005")
    void bubbleSort_emptyArray() {
        BizException e = assertThrows(BizException.class, () -> demoService.bubbleSort(Collections.emptyList()));
        assertEquals(ResultCode.DEMO_0005, e.getResultCode());
    }

    @Test
    @DisplayName("bubbleSort - null 数组抛 DEMO_0005")
    void bubbleSort_nullArray() {
        BizException e = assertThrows(BizException.class, () -> demoService.bubbleSort(null));
        assertEquals(ResultCode.DEMO_0005, e.getResultCode());
    }

    @Test
    @DisplayName("bubbleSort - 逆序数组正确排序")
    void bubbleSort_reverseOrder() {
        List<Integer> numbers = Arrays.asList(9, 7, 5, 3, 1);
        BubbleSortResponse response = demoService.bubbleSort(numbers);
        assertEquals(Arrays.asList(1, 3, 5, 7, 9), response.getSorted());
    }

    @Test
    @DisplayName("bubbleSort - 原始数组不被修改")
    void bubbleSort_originalNotMutated() {
        List<Integer> numbers = Arrays.asList(3, 1, 2);
        demoService.bubbleSort(numbers);
        assertEquals(Arrays.asList(3, 1, 2), numbers);
    }
}

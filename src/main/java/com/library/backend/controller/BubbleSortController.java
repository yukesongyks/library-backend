package com.library.backend.controller;

import com.library.backend.dto.ApiRequest;
import com.library.backend.dto.ApiResponse;
import com.library.backend.service.BubbleSortService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 冒泡排序接口
 *
 * @author AI
 * @date 2026/11/04
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BubbleSortController {

    /** 请求参数错误码 */
    private static final int BAD_REQUEST_CODE = 400;

    private final BubbleSortService bubbleSortService;

    /**
     * 对请求体中的数组执行升序冒泡排序
     *
     * @param request 请求体，array 字段为待排序数组
     * @return 原始数组、排序结果与时间戳
     * @throws IllegalArgumentException 当数组元素包含 null 时抛出
     */
    @PostMapping("/bubble-sort")
    public ApiResponse<Map<String, Object>> bubbleSort(@RequestBody ApiRequest request) {
        List<Integer> values = request.getArray();
        if (values == null) {
            return ApiResponse.error(BAD_REQUEST_CODE, "array不能为空");
        }
        int[] array = toIntArray(values);

        int[] sorted = bubbleSortService.sort(array);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("original", values);
        result.put("sorted", Arrays.stream(sorted).boxed().toList());
        result.put("timestamp", Instant.now().toString());

        return ApiResponse.success(result);
    }

    /**
     * 将请求体中的列表转换为基本类型数组
     *
     * @param values 请求数组
     * @return int 数组
     * @throws IllegalArgumentException 当数组元素包含 null 时抛出
     */
    private int[] toIntArray(List<Integer> values) {
        int[] array = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            Integer value = values.get(i);
            if (value == null) {
                throw new IllegalArgumentException("array元素不能为null");
            }
            array[i] = value;
        }
        return array;
    }
}

package com.library.backend.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 冒泡排序服务
 *
 * @author AI
 * @date 2026/11/04
 */
@Service
public class BubbleSortService {

    /**
     * 对整型数组执行升序冒泡排序
     *
     * <p>入参不会被修改，返回值是排序后的新数组；已有序时通过无交换标记提前结束。</p>
     *
     * @param array 待排序数组，不能为 null
     * @return 升序排序后的新数组
     * @throws IllegalArgumentException 当入参为 null 时抛出
     */
    public int[] sort(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("待排序数组不能为null");
        }

        int[] result = Arrays.copyOf(array, array.length);
        int length = result.length;

        for (int i = 0; i < length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < length - i - 1; j++) {
                if (result[j] > result[j + 1]) {
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                    swapped = true;
                }
            }
            // 本轮无交换说明已是升序，提前结束
            if (!swapped) {
                break;
            }
        }

        return result;
    }
}

package com.library.common.util;

/**
 * 冒泡排序工具类
 * <p>
 * 采用相邻元素两两比较、逐轮将较大值冒泡至未排序区间末尾的原地升序排序算法，
 * 并通过“本轮未发生交换”判定剩余区间已有序，提前结束排序。
 *
 * @author library
 * @date 2026/11/04
 */
public final class BubbleSort {

    private BubbleSort() {
    }

    /**
     * 对整数数组进行升序冒泡排序
     * <p>
     * 排序为原地操作，入参数组的内容会被修改。
     *
     * @param source 待排序数组，不允许为 null
     * @return 排序后的数组，与入参为同一数组引用
     * @throws IllegalArgumentException 当入参数组为 null 时
     */
    public static int[] sort(int[] source) {
        if (source == null) {
            throw new IllegalArgumentException("待排序数组不能为 null");
        }

        // 每轮比较将未排序区间中的最大值冒泡到区间末尾
        for (int end = source.length - 1; end > 0; end--) {
            boolean swapped = false;
            for (int i = 0; i < end; i++) {
                if (source[i] > source[i + 1]) {
                    int temp = source[i];
                    source[i] = source[i + 1];
                    source[i + 1] = temp;
                    swapped = true;
                }
            }

            // 本轮未发生交换，说明剩余区间已有序，可提前结束
            if (!swapped) {
                break;
            }
        }

        return source;
    }
}

package com.library.backend.dto;

import java.util.List;

/**
 * 冒泡排序请求体。
 *
 * <p>spec {@code algorithms.md}：空数组合法，返回 {@code {"result":[],"input":[]}}。
 * 因此 {@code numbers} 不加 {@code @NotEmpty}，仅做非空校验（JSON 缺失字段时框架兜底）。</p>
 */
public class BubbleSortRequest {

    private List<Integer> numbers;

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}

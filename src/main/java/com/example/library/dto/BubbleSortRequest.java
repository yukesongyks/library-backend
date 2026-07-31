package com.example.library.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 冒泡排序请求 DTO。对应 spec POST /api/bubble-sort。
 */
public class BubbleSortRequest {

    @NotNull(message = "numbers 不能为空")
    private List<Integer> numbers;

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}

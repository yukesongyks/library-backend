package com.library.backend.demo.dto;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 冒泡排序请求
 */
public class BubbleSortRequest {

    @NotEmpty(message = "numbers 不能为空")
    private List<Integer> numbers;

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}

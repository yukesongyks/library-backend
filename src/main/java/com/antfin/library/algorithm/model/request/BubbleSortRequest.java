package com.antfin.library.algorithm.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 冒泡排序请求
 */
public class BubbleSortRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "待排序数组不能为空")
    @Size(min = 1, max = 1000, message = "数组长度须为1~1000")
    private List<Integer> numbers;

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}

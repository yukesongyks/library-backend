package com.antdigital.library.demo.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 冒泡排序结果视图对象。
 *
 * @author library-backend
 */
public class BubbleSortVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排序前输入数组 */
    private List<Integer> input;

    /** 排序后结果数组 */
    private List<Integer> output;

    /** 数组长度 */
    private Integer size;

    /** 排序耗时（毫秒） */
    private Long costMs;

    public BubbleSortVO() {
    }

    public BubbleSortVO(List<Integer> input, List<Integer> output, Integer size, Long costMs) {
        this.input = input;
        this.output = output;
        this.size = size;
        this.costMs = costMs;
    }

    public List<Integer> getInput() {
        return input;
    }

    public void setInput(List<Integer> input) {
        this.input = input;
    }

    public List<Integer> getOutput() {
        return output;
    }

    public void setOutput(List<Integer> output) {
        this.output = output;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getCostMs() {
        return costMs;
    }

    public void setCostMs(Long costMs) {
        this.costMs = costMs;
    }

    @Override
    public String toString() {
        return "BubbleSortVO{"
                + "input=" + input
                + ", output=" + output
                + ", size=" + size
                + ", costMs=" + costMs
                + '}';
    }
}

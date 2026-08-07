package com.antfin.library.algorithm.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 冒泡排序返回 VO
 */
public class BubbleSortResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> sortedArray;
    private Long compareCount;
    private Long swapCount;
    private Long durationMillis;

    public BubbleSortResultVO() {
    }

    public BubbleSortResultVO(List<Integer> sortedArray, Long compareCount, Long swapCount, Long durationMillis) {
        this.sortedArray = sortedArray;
        this.compareCount = compareCount;
        this.swapCount = swapCount;
        this.durationMillis = durationMillis;
    }

    public List<Integer> getSortedArray() {
        return sortedArray;
    }

    public void setSortedArray(List<Integer> sortedArray) {
        this.sortedArray = sortedArray;
    }

    public Long getCompareCount() {
        return compareCount;
    }

    public void setCompareCount(Long compareCount) {
        this.compareCount = compareCount;
    }

    public Long getSwapCount() {
        return swapCount;
    }

    public void setSwapCount(Long swapCount) {
        this.swapCount = swapCount;
    }

    public Long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }
}

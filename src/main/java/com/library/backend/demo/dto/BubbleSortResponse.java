package com.library.backend.demo.dto;

import java.util.List;

/**
 * 冒泡排序响应
 */
public class BubbleSortResponse {

    private List<Integer> original;
    private List<Integer> sorted;

    public BubbleSortResponse() {
    }

    public BubbleSortResponse(List<Integer> original, List<Integer> sorted) {
        this.original = original;
        this.sorted = sorted;
    }

    public List<Integer> getOriginal() {
        return original;
    }

    public void setOriginal(List<Integer> original) {
        this.original = original;
    }

    public List<Integer> getSorted() {
        return sorted;
    }

    public void setSorted(List<Integer> sorted) {
        this.sorted = sorted;
    }
}

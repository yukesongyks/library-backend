package com.example.library.dto;

import java.util.List;

/**
 * Response DTO for bubble sort API containing the input, sorted result,
 * step-by-step process, and comparison/swap statistics.
 */
public class BubbleSortResponse {

    private List<Integer> input;
    private List<Integer> sorted;
    private List<SortStep> steps;
    private int comparisons;
    private int swaps;

    public BubbleSortResponse() {
    }

    public BubbleSortResponse(
            List<Integer> input,
            List<Integer> sorted,
            List<SortStep> steps,
            int comparisons,
            int swaps) {
        this.input = input;
        this.sorted = sorted;
        this.steps = steps;
        this.comparisons = comparisons;
        this.swaps = swaps;
    }

    public List<Integer> getInput() {
        return input;
    }

    public void setInput(List<Integer> input) {
        this.input = input;
    }

    public List<Integer> getSorted() {
        return sorted;
    }

    public void setSorted(List<Integer> sorted) {
        this.sorted = sorted;
    }

    public List<SortStep> getSteps() {
        return steps;
    }

    public void setSteps(List<SortStep> steps) {
        this.steps = steps;
    }

    public int getComparisons() {
        return comparisons;
    }

    public void setComparisons(int comparisons) {
        this.comparisons = comparisons;
    }

    public int getSwaps() {
        return swaps;
    }

    public void setSwaps(int swaps) {
        this.swaps = swaps;
    }
}
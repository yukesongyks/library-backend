package com.library.demo.model.response;

import java.time.LocalDateTime;
import java.util.List;

public class BubbleSortResponse {
    private List<Integer> original;
    private List<Integer> sorted;
    private String order;
    private List<List<Integer>> steps;
    private LocalDateTime timestamp;

    public BubbleSortResponse(List<Integer> original, List<Integer> sorted, String order,
                              List<List<Integer>> steps, LocalDateTime timestamp) {
        this.original = original;
        this.sorted = sorted;
        this.order = order;
        this.steps = steps;
        this.timestamp = timestamp;
    }

    public List<Integer> getOriginal() { return original; }
    public List<Integer> getSorted() { return sorted; }
    public String getOrder() { return order; }
    public List<List<Integer>> getSteps() { return steps; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

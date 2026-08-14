package com.library.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BubbleSortResult {
    private List<Integer> original;
    private List<Integer> sorted;
    private String order;
    private int swapCount;
    private String timestamp;
    private long executionTimeMs;
}

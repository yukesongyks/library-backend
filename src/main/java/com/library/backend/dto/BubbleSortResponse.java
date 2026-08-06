package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BubbleSortResponse {
    private List<Integer> sorted;
    private int comparisons;
    private int swaps;
}

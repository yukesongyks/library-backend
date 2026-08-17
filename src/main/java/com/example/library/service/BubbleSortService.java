package com.example.library.service;

import com.example.library.dto.BubbleSortResponse;
import com.example.library.dto.SortStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for performing bubble sort on integer arrays and recording the sorting process.
 */
@Service
public class BubbleSortService {

    /**
     * Sorts the given integer list using bubble sort and records each step.
     *
     * @param input the list of integers to sort (must not be empty, max 100 elements)
     * @return BubbleSortResponse containing input, sorted result, steps, comparisons, and swaps
     * @throws IllegalArgumentException if input is empty or exceeds 100 elements
     */
    public BubbleSortResponse sort(List<Integer> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Array must not be empty and length must not exceed 100");
        }
        if (input.size() > 100) {
            throw new IllegalArgumentException("Array must not be empty and length must not exceed 100");
        }
        for (Integer v : input) {
            if (v == null) {
                throw new IllegalArgumentException("Array contains null element");
            }
        }

        List<Integer> arr = new ArrayList<>(input);
        int n = arr.size();
        List<SortStep> steps = new ArrayList<>();
        int comparisons = 0;
        int swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                comparisons++;
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swaps++;
                    swapped = true;
                }
            }
            steps.add(new SortStep(i + 1, new ArrayList<>(arr), swapped));
            if (!swapped) {
                break;
            }
        }

        return new BubbleSortResponse(new ArrayList<>(input), arr, steps, comparisons, swaps);
    }
}
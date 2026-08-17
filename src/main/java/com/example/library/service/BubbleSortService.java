package com.example.library.service;

import com.example.library.dto.BubbleSortResponse;
import com.example.library.dto.SortStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortService {

    public BubbleSortResponse sort(List<Integer> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Array must not be empty and length must not exceed 100");
        }
        if (input.size() > 100) {
            throw new IllegalArgumentException("Array must not be empty and length must not exceed 100");
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
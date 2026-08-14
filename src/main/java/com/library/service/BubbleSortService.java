package com.library.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class BubbleSortService {

    public BubbleSortResult sort(int[] array, String order) {
        int[] sorted = Arrays.copyOf(array, array.length);
        int steps = 0;
        int n = sorted.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                steps++;
                boolean shouldSwap = "asc".equals(order)
                        ? sorted[j] > sorted[j + 1]
                        : sorted[j] < sorted[j + 1];
                if (shouldSwap) {
                    int tmp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return new BubbleSortResult(sorted, steps);
    }

    public record BubbleSortResult(int[] sorted, int steps) {}
}
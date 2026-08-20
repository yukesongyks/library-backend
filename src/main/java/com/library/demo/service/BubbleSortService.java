package com.library.demo.service;

import com.library.demo.model.response.BubbleSortResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortService {

    public BubbleSortResponse sort(List<Integer> numbers, String order) {
        String sortOrder = (order == null || order.isBlank()) ? "ASC" : order.toUpperCase();
        if (!sortOrder.equals("ASC") && !sortOrder.equals("DESC")) {
            throw new IllegalArgumentException("order must be ASC or DESC");
        }

        List<Integer> original = new ArrayList<>(numbers);
        List<Integer> arr = new ArrayList<>(numbers);
        List<List<Integer>> steps = new ArrayList<>();
        int n = arr.size();

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                boolean shouldSwap = sortOrder.equals("ASC")
                        ? arr.get(j) > arr.get(j + 1)
                        : arr.get(j) < arr.get(j + 1);
                if (shouldSwap) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapped = true;
                }
            }
            steps.add(new ArrayList<>(arr));
            if (!swapped) break;
        }

        return new BubbleSortResponse(original, arr, sortOrder, steps, LocalDateTime.now());
    }
}

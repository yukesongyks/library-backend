package com.library.demo.service;

import com.library.demo.dto.request.BubbleSortRequest;
import com.library.demo.dto.response.BubbleSortResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortService {

    public BubbleSortResult sort(BubbleSortRequest request) {
        long start = System.currentTimeMillis();

        String order = (request.getOrder() != null && !request.getOrder().isBlank())
                ? request.getOrder() : "ASC";
        boolean ascending = "ASC".equalsIgnoreCase(order);

        List<Integer> original = new ArrayList<>(request.getNumbers());
        List<Integer> arr = new ArrayList<>(request.getNumbers());
        int swapCount = bubbleSort(arr, ascending);

        long elapsed = System.currentTimeMillis() - start;

        return new BubbleSortResult(
                original,
                arr,
                order,
                swapCount,
                Instant.now().toString(),
                elapsed
        );
    }

    private int bubbleSort(List<Integer> arr, boolean ascending) {
        int n = arr.size();
        int swapCount = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                boolean shouldSwap = ascending
                        ? arr.get(j) > arr.get(j + 1)
                        : arr.get(j) < arr.get(j + 1);
                if (shouldSwap) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapCount++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return swapCount;
    }
}

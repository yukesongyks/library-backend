package com.library.backend.service;

import com.library.backend.dto.BubbleSortResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortService {

    public BubbleSortResponse sort(List<Integer> numbers) {
        List<Integer> arr = new ArrayList<>(numbers);
        int n = arr.size();
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
            if (!swapped) {
                break;
            }
        }

        return new BubbleSortResponse(arr, comparisons, swaps);
    }
}

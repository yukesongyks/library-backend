package com.library.backend.model.dto;

import java.util.List;

/**
 * Bubble sort response data (design spec §4.4).
 *
 * @param input  original input array
 * @param sorted sorted array (ascending)
 * @param swaps  count of adjacent swaps performed by the hand-written bubble sort
 */
public record BubbleSortResponse(List<Integer> input, List<Integer> sorted, int swaps) {
}

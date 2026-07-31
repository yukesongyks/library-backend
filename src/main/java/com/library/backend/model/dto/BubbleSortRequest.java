package com.library.backend.model.dto;

import java.util.List;

/**
 * Bubble sort request body (design spec §4.4).
 *
 * <p>Per-interface validation (§5.2.2) is performed as a business precheck in the
 * service layer (throws {@code IllegalArgumentException} → code 40002):
 * <ul>
 *   <li>empty {@code numbers} → 40002 "numbers 不能为空"</li>
 *   <li>{@code numbers.size() > 1000} → 40002 "数组长度超限(≤1000)"</li>
 * </ul>
 *
 * @param numbers integer list to sort
 */
public record BubbleSortRequest(List<Integer> numbers) {
}

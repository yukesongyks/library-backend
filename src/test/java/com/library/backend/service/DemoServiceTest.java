package com.library.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.library.backend.model.dto.BubbleSortResponse;
import com.library.backend.model.dto.HashResponse;
import com.library.backend.model.dto.HelloWorldResponse;

/**
 * Unit tests for {@link DemoService}.
 *
 * <p>Not executed in this environment (no mvn). Kept compile-correct: imports,
 * types and assertions match the service API. Key assertions:
 * <ul>
 *   <li>bubble sort on {@code [5,2,9,1,5,6]} yields swaps == 6 (corrected value;
 *       the design example's 8 is wrong),</li>
 *   <li>SHA-256 digest length == 64.</li>
 * </ul>
 */
class DemoServiceTest {

    private final DemoService service = new DemoService();

    @Test
    void helloWorldReturnsFixedMessage() {
        HelloWorldResponse resp = service.helloWorld();
        assertEquals("Hello, World!", resp.message());
    }

    @Test
    void hashProducesSha256LowercaseHexOfLength64() {
        HashResponse resp = service.hash("Hello, World!");
        assertEquals("Hello, World!", resp.original());
        assertEquals("SHA-256", resp.algorithm());
        // SHA-256 digest is 32 bytes -> 64 hex chars, lowercase (§4.3, §5.2.3)
        assertEquals(64, resp.digest().length(), "digest must be 64 hex chars");
        assertEquals(resp.digest().toLowerCase(), resp.digest(), "digest must be lowercase");
    }

    @Test
    void hashRejectsEmptyTextWithBusinessMessage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.hash(""));
        assertEquals("text 不能为空", ex.getMessage());
    }

    @Test
    void hashRejectsNullText() {
        assertThrows(IllegalArgumentException.class, () -> service.hash(null));
    }

    @Test
    void hashRejectsTooLongText() {
        String tooLong = "a".repeat(1001);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.hash(tooLong));
        assertEquals("text 长度超限(≤1000)", ex.getMessage());
    }

    @Test
    void bubbleSortOnSpecInputProducesSwaps6Not8() {
        // Pre-flight correction: the design example claims swaps=8, which is WRONG.
        // A correct adjacent-swap bubble sort on [5,2,9,1,5,6] produces swaps=6.
        BubbleSortResponse resp = service.bubbleSort(List.of(5, 2, 9, 1, 5, 6));
        assertIterableEquals(List.of(5, 2, 9, 1, 5, 6), resp.input());
        assertIterableEquals(List.of(1, 2, 5, 5, 6, 9), resp.sorted());
        assertEquals(6, resp.swaps(), "correct bubble sort swap count is 6, not the spec example's 8");
    }

    @Test
    void bubbleSortRejectsEmptyList() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.bubbleSort(List.of()));
        assertEquals("numbers 不能为空", ex.getMessage());
    }

    @Test
    void bubbleSortRejectsNullList() {
        assertThrows(IllegalArgumentException.class, () -> service.bubbleSort(null));
    }

    @Test
    void bubbleSortRejectsTooLongList() {
        Integer[] arr = new Integer[1001];
        java.util.Arrays.fill(arr, 1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.bubbleSort(List.of(arr)));
        assertEquals("数组长度超限(≤1000)", ex.getMessage());
    }

    @Test
    void bubbleSortAlreadySortedHasZeroSwaps() {
        BubbleSortResponse resp = service.bubbleSort(List.of(1, 2, 3, 4));
        assertEquals(0, resp.swaps());
        assertIterableEquals(List.of(1, 2, 3, 4), resp.sorted());
    }

    @Test
    void bubbleSortDescendingInputCountsMaxSwaps() {
        BubbleSortResponse resp = service.bubbleSort(List.of(4, 3, 2, 1));
        // n=4 -> 6 adjacent swaps to fully reverse
        assertEquals(6, resp.swaps());
        assertIterableEquals(List.of(1, 2, 3, 4), resp.sorted());
    }
}

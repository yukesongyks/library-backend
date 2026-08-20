package com.library.demo.service;

import com.library.demo.model.response.BubbleSortResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSortServiceTest {

    private final BubbleSortService service = new BubbleSortService();

    @Test
    void sort_ascending_returnsSortedArray() {
        BubbleSortResponse response = service.sort(List.of(5, 3, 8, 1, 9, 2), "ASC");
        assertEquals(List.of(1, 2, 3, 5, 8, 9), response.getSorted());
        assertEquals("ASC", response.getOrder());
        assertEquals(List.of(5, 3, 8, 1, 9, 2), response.getOriginal());
        assertFalse(response.getSteps().isEmpty());
    }

    @Test
    void sort_descending_returnsReverseSortedArray() {
        BubbleSortResponse response = service.sort(List.of(3, 1, 2), "DESC");
        assertEquals(List.of(3, 2, 1), response.getSorted());
    }

    @Test
    void sort_defaultOrder_usesAscending() {
        BubbleSortResponse response = service.sort(List.of(3, 1, 2), null);
        assertEquals(List.of(1, 2, 3), response.getSorted());
        assertEquals("ASC", response.getOrder());
    }

    @Test
    void sort_invalidOrder_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.sort(List.of(1, 2), "RANDOM"));
    }

    @Test
    void sort_singleElement_returnsSameElement() {
        BubbleSortResponse response = service.sort(List.of(42), "ASC");
        assertEquals(List.of(42), response.getSorted());
    }
}

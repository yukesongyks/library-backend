package com.library.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link BubbleSortService} 单元测试
 *
 * @author AI
 * @date 2026/11/04
 */
class BubbleSortServiceTest {

    private final BubbleSortService bubbleSortService = new BubbleSortService();

    // ==================== sort 测试 ====================

    @Test
    @DisplayName("无序数组应返回升序结果")
    void should_returnAscendingArray_when_arrayUnsorted() {
        // Arrange (Given)
        int[] array = {5, 3, 8, 1, 2};

        // Act (When)
        int[] actual = bubbleSortService.sort(array);

        // Assert (Then)
        assertThat(actual).as("无序数组排序结果").containsExactly(1, 2, 3, 5, 8);
    }

    @Test
    @DisplayName("包含负数与重复元素的数组应返回升序结果")
    void should_returnAscendingArray_when_arrayContainsNegativeAndDuplicateValues() {
        // Arrange (Given)
        int[] array = {-1, 3, 0, -1, 2, 2};

        // Act (When)
        int[] actual = bubbleSortService.sort(array);

        // Assert (Then)
        assertThat(actual).as("含负数与重复元素排序结果").containsExactly(-1, -1, 0, 2, 2, 3);
    }

    @Test
    @DisplayName("已升序数组应保持原顺序")
    void should_keepOrder_when_arrayAlreadyAscending() {
        // Arrange (Given)
        int[] array = {1, 2, 3, 4, 5};

        // Act (When)
        int[] actual = bubbleSortService.sort(array);

        // Assert (Then)
        assertThat(actual).as("已升序数组排序结果").containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("逆序数组应返回升序结果")
    void should_returnAscendingArray_when_arrayDescending() {
        // Arrange (Given)
        int[] array = {5, 4, 3, 2, 1};

        // Act (When)
        int[] actual = bubbleSortService.sort(array);

        // Assert (Then)
        assertThat(actual).as("逆序数组排序结果").containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("单元素数组应原样返回")
    void should_returnSameArray_when_arrayHasSingleElement() {
        // Arrange (Given)
        int[] array = {7};

        // Act (When)
        int[] actual = bubbleSortService.sort(array);

        // Assert (Then)
        assertThat(actual).as("单元素数组排序结果").containsExactly(7);
    }

    @Test
    @DisplayName("空数组应返回空数组")
    void should_returnEmptyArray_when_arrayIsEmpty() {
        // Arrange (Given)
        int[] array = new int[0];

        // Act (When)
        int[] actual = bubbleSortService.sort(array);

        // Assert (Then)
        assertThat(actual).as("空数组排序结果").isEmpty();
    }

    @Test
    @DisplayName("排序不应修改入参数组")
    void should_notModifyInput_when_sort() {
        // Arrange (Given)
        int[] array = {4, 2, 9};
        int[] expectedOriginal = {4, 2, 9};

        // Act (When)
        bubbleSortService.sort(array);

        // Assert (Then)
        assertThat(array).as("入参数组应保持不变").containsExactly(expectedOriginal);
    }

    @Test
    @DisplayName("入参为 null 时应抛出 IllegalArgumentException")
    void should_throwException_when_arrayIsNull() {
        // Arrange (Given)
        int[] array = null;

        // Act & Assert (When & Then)
        assertThatThrownBy(() -> bubbleSortService.sort(array))
                .as("null 入参应抛出参数异常")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能为null");
    }
}

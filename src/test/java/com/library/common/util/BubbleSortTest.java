package com.library.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link BubbleSort} 单元测试
 *
 * @author library
 * @date 2026/11/04
 */
class BubbleSortTest {

    // ==================== sort 测试 ====================

    @Test
    @DisplayName("乱序数组应升序排序")
    void should_returnAscendingArray_when_inputIsUnordered() {
        // Arrange
        int[] source = {5, 1, 4, 2, 8};

        // Act
        int[] actual = BubbleSort.sort(source);

        // Assert
        assertThat(actual).as("乱序数组排序结果").containsExactly(1, 2, 4, 5, 8);
    }

    @Test
    @DisplayName("完全逆序数组应升序排序")
    void should_returnAscendingArray_when_inputIsDescending() {
        // Arrange
        int[] source = {9, 7, 5, 3, 1};

        // Act
        int[] actual = BubbleSort.sort(source);

        // Assert
        assertThat(actual).as("逆序数组排序结果").containsExactly(1, 3, 5, 7, 9);
    }

    @Test
    @DisplayName("已升序数组应保持原顺序")
    void should_keepOriginalOrder_when_inputIsAlreadyAscending() {
        // Arrange
        int[] source = {1, 2, 3, 4, 5};

        // Act
        int[] actual = BubbleSort.sort(source);

        // Assert
        assertThat(actual).as("有序数组排序结果").containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("含重复值与负数时应正确排序")
    void should_returnAscendingArray_when_inputHasDuplicatesAndNegatives() {
        // Arrange
        int[] source = {3, -1, 3, 0, -5, 0};

        // Act
        int[] actual = BubbleSort.sort(source);

        // Assert
        assertThat(actual).as("重复值与负数排序结果").containsExactly(-5, -1, 0, 0, 3, 3);
    }

    @Test
    @DisplayName("空数组应直接返回空数组")
    void should_returnEmptyArray_when_inputIsEmpty() {
        // Arrange
        int[] source = {};

        // Act
        int[] actual = BubbleSort.sort(source);

        // Assert
        assertThat(actual).as("空数组排序结果").isEmpty();
    }

    @Test
    @DisplayName("单元素数组应原样返回")
    void should_returnSameElement_when_inputHasSingleElement() {
        // Arrange
        int[] source = {42};

        // Act
        int[] actual = BubbleSort.sort(source);

        // Assert
        assertThat(actual).as("单元素数组排序结果").containsExactly(42);
    }

    @Test
    @DisplayName("排序应为原地操作并返回同一数组引用")
    void should_sortInPlace_when_inputArrayProvided() {
        // Arrange
        int[] source = {2, 1};

        // Act
        int[] actual = BubbleSort.sort(source);

        // Assert
        assertThat(actual).as("返回值应与入参为同一引用").isSameAs(source);
        assertThat(source).as("入参数组应被原地排序").containsExactly(1, 2);
    }

    @Test
    @DisplayName("入参为 null 时应抛出 IllegalArgumentException")
    void should_throwIllegalArgumentException_when_inputIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> BubbleSort.sort(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("待排序数组不能为 null");
    }

    @ParameterizedTest(name = "边界用例: {0}")
    @MethodSource("provideBoundaryArrays")
    @DisplayName("边界值数组应正确排序")
    void should_returnAscendingArray_when_inputMatchesBoundaryCases(int[] source, int[] expected) {
        // Act
        int[] actual = BubbleSort.sort(source);

        // Assert
        assertThat(actual).containsExactly(expected);
    }

    private static Stream<Arguments> provideBoundaryArrays() {
        return Stream.of(
                Arguments.of(new int[]{0}, new int[]{0}),
                Arguments.of(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0},
                        new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}),
                Arguments.of(new int[]{7, 7, 7, 7}, new int[]{7, 7, 7, 7})
        );
    }
}

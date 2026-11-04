package com.library.backend.controller;

import com.library.backend.dto.ApiRequest;
import com.library.backend.dto.ApiResponse;
import com.library.backend.service.BubbleSortService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link BubbleSortController} 单元测试
 *
 * @author AI
 * @date 2026/11/04
 */
class BubbleSortControllerTest {

    private final BubbleSortController controller = new BubbleSortController(new BubbleSortService());

    // ==================== bubbleSort 测试 ====================

    @Test
    @DisplayName("正常数组应返回原始值与升序结果")
    void should_returnOriginalAndSorted_when_arrayValid() {
        // Arrange (Given)
        ApiRequest request = ApiRequest.builder()
                .array(Arrays.asList(5, 3, 8, 1, 2))
                .build();

        // Act (When)
        ApiResponse<Map<String, Object>> response = controller.bubbleSort(request);

        // Assert (Then)
        assertThat(response.getCode()).as("响应码").isEqualTo(200);
        assertThat(response.getData().get("original")).as("原始数组").isEqualTo(Arrays.asList(5, 3, 8, 1, 2));
        assertThat(response.getData().get("sorted")).as("排序结果").isEqualTo(Arrays.asList(1, 2, 3, 5, 8));
        assertThat(response.getData().get("timestamp")).as("时间戳").isNotNull();
    }

    @Test
    @DisplayName("空数组应返回空结果")
    void should_returnEmptyLists_when_arrayIsEmpty() {
        // Arrange (Given)
        ApiRequest request = ApiRequest.builder()
                .array(List.of())
                .build();

        // Act (When)
        ApiResponse<Map<String, Object>> response = controller.bubbleSort(request);

        // Assert (Then)
        assertThat(response.getCode()).as("响应码").isEqualTo(200);
        assertThat((List<?>) response.getData().get("sorted")).as("排序结果").isEmpty();
    }

    @Test
    @DisplayName("array 缺失时应返回 400 错误")
    void should_returnError_when_arrayIsNull() {
        // Arrange (Given)
        ApiRequest request = ApiRequest.builder().build();

        // Act (When)
        ApiResponse<Map<String, Object>> response = controller.bubbleSort(request);

        // Assert (Then)
        assertThat(response.getCode()).as("响应码").isEqualTo(400);
        assertThat(response.getMessage()).as("错误信息").contains("array");
    }

    @Test
    @DisplayName("数组元素为 null 时应抛出 IllegalArgumentException")
    void should_throwException_when_arrayContainsNullElement() {
        // Arrange (Given)
        ApiRequest request = ApiRequest.builder()
                .array(Arrays.asList(3, null, 1))
                .build();

        // Act & Assert (When & Then)
        assertThatThrownBy(() -> controller.bubbleSort(request))
                .as("null 元素应抛出参数异常")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能为null");
    }
}

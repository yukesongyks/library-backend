package com.antdigital.library.demo.service.impl;

import com.antdigital.library.common.exception.ServiceException;
import com.antdigital.library.demo.model.vo.BubbleSortVO;
import com.antdigital.library.demo.model.vo.HashVO;
import com.antdigital.library.demo.model.vo.HelloWorldVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DemoServiceImpl 单元测试。
 *
 * <p>遵循 AAA 模式（Arrange-Act-Assert），覆盖正常路径、参数校验、边界值。</p>
 *
 * @author library-backend
 */
@DisplayName("DemoServiceImpl 单元测试")
class DemoServiceImplTest {

    private DemoServiceImpl demoService;

    @BeforeEach
    void setUp() {
        demoService = new DemoServiceImpl();
    }

    // ==================== helloWorld ====================

    @Test
    @DisplayName("helloWorld 正常路径：返回欢迎消息和时间戳")
    void should_returnMessage_when_helloWorld() {
        // Arrange
        // Act
        HelloWorldVO result = demoService.helloWorld();

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getMessage()),
                () -> assertFalse(result.getMessage().isBlank()),
                () -> assertNotNull(result.getTimestamp())
        );
    }

    // ==================== hash ====================

    @Test
    @DisplayName("hash 正常路径：返回 SHA-256 哈希值")
    void should_returnHashValue_when_validInput() {
        // Arrange
        String input = "hello";

        // Act
        HashVO result = demoService.hash(input);

        // Assert
        String expectedSha256 = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(input, result.getInput()),
                () -> assertEquals("SHA-256", result.getAlgorithm()),
                () -> assertEquals(expectedSha256, result.getHashValue())
        );
    }

    @Test
    @DisplayName("hash 边界值：空字符串应抛出参数异常")
    void should_throwException_when_inputBlank() {
        // Arrange
        String input = "";

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> demoService.hash(input));
        assertTrue(exception.getMessage().contains("参数"));
    }

    @Test
    @DisplayName("hash 正常路径：空格字符串仍可哈希")
    void should_returnHashValue_when_inputSpace() {
        // Arrange
        String input = "   ";

        // Act
        HashVO result = demoService.hash(input);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getHashValue());
    }

    // ==================== bubbleSort ====================

    @Test
    @DisplayName("bubbleSort 正常路径：无序数组排序后为升序")
    void should_returnSortedArray_when_unorderedInput() {
        // Arrange
        String input = "5,3,8,1,9,2";

        // Act
        BubbleSortVO result = demoService.bubbleSort(input);

        // Assert
        List<Integer> expected = List.of(1, 2, 3, 5, 8, 9);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expected, result.getOutput()),
                () -> assertEquals(6, result.getSize())
        );
    }

    @Test
    @DisplayName("bubbleSort 边界值：已排序数组保持不变")
    void should_keepOrder_when_alreadySorted() {
        // Arrange
        String input = "1,2,3,4,5";

        // Act
        BubbleSortVO result = demoService.bubbleSort(input);

        // Assert
        assertEquals(List.of(1, 2, 3, 4, 5), result.getOutput());
    }

    @Test
    @DisplayName("bubbleSort 边界值：单元素数组")
    void should_returnSingle_when_oneElement() {
        // Arrange
        String input = "42";

        // Act
        BubbleSortVO result = demoService.bubbleSort(input);

        // Assert
        assertAll(
                () -> assertEquals(List.of(42), result.getOutput()),
                () -> assertEquals(1, result.getSize())
        );
    }

    @Test
    @DisplayName("bubbleSort 边界值：空字符串应抛出参数异常")
    void should_throwException_when_emptyInput() {
        // Arrange
        String input = "";

        // Act & Assert
        assertThrows(ServiceException.class, () -> demoService.bubbleSort(input));
    }

    @Test
    @DisplayName("bubbleSort 参数校验：非数字元素应抛出异常")
    void should_throwException_when_nonNumericInput() {
        // Arrange
        String input = "1,a,3";

        // Act & Assert
        assertThrows(ServiceException.class, () -> demoService.bubbleSort(input));
    }
}

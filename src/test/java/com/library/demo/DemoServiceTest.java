package com.library.demo;

import com.library.common.exception.BizException;
import com.library.demo.model.DemoResult;
import com.library.demo.model.HashRequest;
import com.library.demo.model.HashResult;
import com.library.demo.model.SortRequest;
import com.library.demo.model.SortResult;
import com.library.demo.service.DemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DemoService 单元测试
 * 采用纯单元测试（不依赖 Spring 容器与数据库），与系分"三接口为无状态纯计算"语义一致，
 * 遵循测试金字塔：纯计算逻辑用快速单元测试覆盖，不引入 @SpringBootTest 重型上下文。
 */
class DemoServiceTest {

    private DemoService demoService;

    @BeforeEach
    void setUp() {
        demoService = new DemoService();
    }

    @Test
    void testHelloworld() {
        DemoResult result = demoService.helloworld();
        assertEquals("Hello, World!", result.getResult());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testHashSha256() {
        HashRequest req = new HashRequest();
        req.setText("hello world");
        req.setAlgorithm("SHA_256");
        HashResult result = demoService.hash(req);
        assertEquals("SHA_256", result.getAlgorithm());
        assertEquals("hello world", result.getInput());
        assertEquals(64, result.getHash().length());
    }

    @Test
    void testHashDefault() {
        HashRequest req = new HashRequest();
        req.setText("test");
        HashResult result = demoService.hash(req);
        assertEquals("SHA_256", result.getAlgorithm());
    }

    @Test
    void testHashEmptyText() {
        HashRequest req = new HashRequest();
        req.setText("");
        BizException ex = assertThrows(BizException.class, () -> demoService.hash(req));
        assertEquals("DEMO_002", ex.getCode());
    }

    @Test
    void testHashUnsupportedAlgorithm() {
        HashRequest req = new HashRequest();
        req.setText("test");
        req.setAlgorithm("UNKNOWN");
        BizException ex = assertThrows(BizException.class, () -> demoService.hash(req));
        assertEquals("DEMO_003", ex.getCode());
    }

    @Test
    void testBubbleSort() {
        SortRequest req = new SortRequest();
        req.setNumbers(Arrays.asList(5, 3, 8, 1, 9, 2));
        SortResult result = demoService.bubbleSort(req);
        assertEquals(Arrays.asList(1, 2, 3, 5, 8, 9), result.getSorted());
        assertEquals(6, result.getSize());
        assertNotNull(result.getCostMs());
    }

    @Test
    void testBubbleSortEmpty() {
        SortRequest req = new SortRequest();
        req.setNumbers(Collections.emptyList());
        BizException ex = assertThrows(BizException.class, () -> demoService.bubbleSort(req));
        assertEquals("DEMO_004", ex.getCode());
    }

    @Test
    void testBubbleSortTooLarge() {
        SortRequest req = new SortRequest();
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 1001; i++) {
            nums.add(i);
        }
        req.setNumbers(nums);
        BizException ex = assertThrows(BizException.class, () -> demoService.bubbleSort(req));
        assertEquals("DEMO_005", ex.getCode());
    }
}

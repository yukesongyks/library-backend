package com.example.library.controller;

import com.example.library.dto.BubbleSortRequest;
import com.example.library.dto.HashRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 三接口单元测试（MockMvc）：覆盖 specs 中 Given/When/Then。
 * 对应 tasks C4。
 */
@SpringBootTest
@AutoConfigureMockMvc
class HelloWorldControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();

    // Scenario: 正常调用 helloworld
    @Test
    void helloWorld_returnsResult() throws Exception {
        mockMvc.perform(get("/api/hello-world").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Hello, World!"));
    }

    // Scenario: SHA-256 哈希
    @Test
    void hash_sha256() throws Exception {
        HashRequest req = new HashRequest();
        req.setAlgorithm("SHA-256");
        req.setInput("abc");
        mockMvc.perform(post("/api/hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm").value("SHA-256"))
                .andExpect(jsonPath("$.input").value("abc"))
                .andExpect(jsonPath("$.hash")
                        .value("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"));
    }

    // Scenario: 不支持的算法
    @Test
    void hash_unsupportedAlgorithm_returns400() throws Exception {
        HashRequest req = new HashRequest();
        req.setAlgorithm("FOO");
        req.setInput("abc");
        mockMvc.perform(post("/api/hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // Scenario: 正常排序
    @Test
    void bubbleSort_normal() throws Exception {
        BubbleSortRequest req = new BubbleSortRequest();
        req.setNumbers(Arrays.asList(3, 1, 2));
        mockMvc.perform(post("/api/bubble-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sorted[0]").value(1))
                .andExpect(jsonPath("$.sorted[1]").value(2))
                .andExpect(jsonPath("$.sorted[2]").value(3))
                .andExpect(jsonPath("$.steps").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    // Scenario: 空数组
    @Test
    void bubbleSort_emptyArray() throws Exception {
        BubbleSortRequest req = new BubbleSortRequest();
        req.setNumbers(Arrays.asList());
        mockMvc.perform(post("/api/bubble-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sorted").isEmpty())
                .andExpect(jsonPath("$.steps").value(0));
    }
}

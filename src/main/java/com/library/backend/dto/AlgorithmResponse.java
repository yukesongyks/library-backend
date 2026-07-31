package com.library.backend.dto;

import java.util.List;

/**
 * 算法接口统一响应体。
 *
 * <p>各算法返回不同字段，通过该载体封装：
 * <ul>
 *   <li>helloworld → {@code {"result": "Hello, World!"}}</li>
 *   <li>hash → {@code {"result": "<hex>", "algorithm": "<name>"}}</li>
 *   <li>bubble-sort → {@code {"result": [...], "input": [...]}}</li>
 * </ul>
 * 使用泛型 {@code T} 承载不同类型的 result（String 或 List）。</p>
 *
 * @param <T> result 字段类型
 */
public class AlgorithmResponse<T> {

    private T result;
    private String algorithm;
    private List<Integer> input;

    public AlgorithmResponse() {
    }

    public AlgorithmResponse(T result) {
        this.result = result;
    }

    public AlgorithmResponse(T result, List<Integer> input) {
        this.result = result;
        this.input = input;
    }

    public AlgorithmResponse(T result, String algorithm) {
        this.result = result;
        this.algorithm = algorithm;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public List<Integer> getInput() {
        return input;
    }

    public void setInput(List<Integer> input) {
        this.input = input;
    }
}

package com.antfin.library.algorithm.service;

import com.antfin.library.algorithm.model.vo.BubbleSortResultVO;
import com.antfin.library.algorithm.model.vo.HashResultVO;
import com.antfin.library.algorithm.model.vo.HelloWorldVO;

import java.util.List;

/**
 * 算法服务
 */
public interface AlgorithmService {

    /**
     * W01 HelloWorld
     *
     * @return 问候消息
     */
    HelloWorldVO helloWorld();

    /**
     * W02 哈希算法
     *
     * @param inputText 输入文本
     * @param algorithm 算法名称（MD5/SHA256/SHA512），默认 SHA256
     * @return 哈希结果
     */
    HashResultVO hash(String inputText, String algorithm);

    /**
     * W03 冒泡排序
     *
     * @param numbers 待排序数组
     * @return 排序结果
     */
    BubbleSortResultVO bubbleSort(List<Integer> numbers);
}

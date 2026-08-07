package com.antdigital.library.demo.service;

import com.antdigital.library.demo.model.vo.BubbleSortVO;
import com.antdigital.library.demo.model.vo.HashVO;
import com.antdigital.library.demo.model.vo.HelloWorldVO;

/**
 * 演示功能服务接口。
 *
 * @author library-backend
 */
public interface DemoService {

    /**
     * HelloWorld 接口。
     *
     * @return 欢迎消息
     */
    HelloWorldVO helloWorld();

    /**
     * 哈希算法接口，计算输入字符串的 SHA-256 哈希。
     *
     * @param input 原始字符串
     * @return 哈希结果
     */
    HashVO hash(String input);

    /**
     * 冒泡排序接口，对逗号分隔的数字串排序。
     *
     * @param input 逗号分隔的数字串
     * @return 排序结果
     */
    BubbleSortVO bubbleSort(String input);
}

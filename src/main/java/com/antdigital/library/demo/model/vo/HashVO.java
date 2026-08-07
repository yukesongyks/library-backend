package com.antdigital.library.demo.model.vo;

import java.io.Serializable;

/**
 * 哈希算法结果视图对象。
 *
 * @author library-backend
 */
public class HashVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 原始输入 */
    private String input;

    /** 哈希算法名称 */
    private String algorithm;

    /** 哈希结果（十六进制） */
    private String hashValue;

    public HashVO() {
    }

    public HashVO(String input, String algorithm, String hashValue) {
        this.input = input;
        this.algorithm = algorithm;
        this.hashValue = hashValue;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    @Override
    public String toString() {
        return "HashVO{"
                + "input='" + input + '\''
                + ", algorithm='" + algorithm + '\''
                + ", hashValue='" + hashValue + '\''
                + '}';
    }
}

package com.antfin.library.algorithm.model.vo;

import java.io.Serializable;

/**
 * 哈希算法返回 VO
 */
public class HashResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hashHex;
    private Integer inputLength;

    public HashResultVO() {
    }

    public HashResultVO(String hashHex, Integer inputLength) {
        this.hashHex = hashHex;
        this.inputLength = inputLength;
    }

    public String getHashHex() {
        return hashHex;
    }

    public void setHashHex(String hashHex) {
        this.hashHex = hashHex;
    }

    public Integer getInputLength() {
        return inputLength;
    }

    public void setInputLength(Integer inputLength) {
        this.inputLength = inputLength;
    }
}

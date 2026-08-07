package com.library.demo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 哈希算法结果
 */
@Data
public class HashResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String algorithm;
    private String input;
    private String hash;
}

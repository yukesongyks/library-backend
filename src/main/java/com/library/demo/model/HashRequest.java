package com.library.demo.model;

import lombok.Data;

/**
 * 哈希算法请求
 */
@Data
public class HashRequest {

    /** 待计算哈希的文本内容 */
    private String text;

    /** 哈希算法，默认 SHA_256，可选 MD5 */
    private String algorithm;
}

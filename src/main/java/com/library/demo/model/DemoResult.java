package com.library.demo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * helloworld 结果
 */
@Data
public class DemoResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String result;
    private String timestamp;
}

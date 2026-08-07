package com.antfin.library.algorithm.model.vo;

import java.io.Serializable;

/**
 * HelloWorld 返回 VO
 */
public class HelloWorldVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    public HelloWorldVO() {
    }

    public HelloWorldVO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

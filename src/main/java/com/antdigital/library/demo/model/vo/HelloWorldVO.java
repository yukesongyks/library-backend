package com.antdigital.library.demo.model.vo;

import java.io.Serializable;

/**
 * HelloWorld 结果视图对象。
 *
 * @author library-backend
 */
public class HelloWorldVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 欢迎消息 */
    private String message;

    /** 服务时间戳（字符串，避免 Long 精度丢失） */
    private String timestamp;

    public HelloWorldVO() {
    }

    public HelloWorldVO(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HelloWorldVO{"
                + "message='" + message + '\''
                + ", timestamp='" + timestamp + '\''
                + '}';
    }
}

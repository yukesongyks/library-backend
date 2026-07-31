package com.antgroup.library.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结构（设计文档 5.0.1）。
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String OK = "OK";
    public static final String SUCCESS_MSG = "SUCCESS";

    private String code;
    private String msg;
    private T data;

    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(OK, SUCCESS_MSG, data);
    }

    public static <T> Result<T> success() {
        return new Result<>(OK, SUCCESS_MSG, null);
    }

    public static <T> Result<T> error(String code, String msg) {
        return new Result<>(code, msg, null);
    }
}

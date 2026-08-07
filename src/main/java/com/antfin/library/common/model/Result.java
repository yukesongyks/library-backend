package com.antfin.library.common.model;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @param <T> 数据类型
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CODE_SUCCESS = "OK";
    public static final String CODE_FAIL = "FAIL";
    public static final String MSG_SUCCESS = "SUCCESS";

    private String code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(CODE_SUCCESS, MSG_SUCCESS, data);
    }

    public static <T> Result<T> success() {
        return new Result<>(CODE_SUCCESS, MSG_SUCCESS, null);
    }

    public static <T> Result<T> fail(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(CODE_FAIL, msg, null);
    }

    public boolean isSuccess() {
        return CODE_SUCCESS.equals(this.code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

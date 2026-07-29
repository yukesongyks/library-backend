package com.library.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结构。
 *
 * @param <T> 业务数据类型
 * @author DTCoder
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 结果code，"OK"表示成功，其余为错误码 */
    private String code;

    /** 提示信息 */
    private String msg;

    /** 业务数据 */
    private T data;

    private Result() {
    }

    private Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应（无数据）。
     *
     * @param <T> 业务数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>("OK", "SUCCESS", null);
    }

    /**
     * 成功响应（带数据）。
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>("OK", "SUCCESS", data);
    }

    /**
     * 失败响应。
     *
     * @param code 错误码
     * @param msg  提示信息
     * @param <T>  业务数据类型
     * @return 失败响应
     */
    public static <T> Result<T> fail(String code, String msg) {
        return new Result<>(code, msg, null);
    }
}

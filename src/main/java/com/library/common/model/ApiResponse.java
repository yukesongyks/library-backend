package com.library.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应结构
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String msg;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode("OK");
        resp.setMsg("SUCCESS");
        resp.setData(data);
        return resp;
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> fail(String code, String msg) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(code);
        resp.setMsg(msg);
        resp.setData(null);
        return resp;
    }
}

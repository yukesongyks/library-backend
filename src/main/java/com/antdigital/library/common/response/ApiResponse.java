package com.antdigital.library.common.response;

import java.io.Serializable;

/**
 * 统一 API 响应包装类。
 *
 * <p>遵循前后端规约：JSON key 使用 lowerCamelCase，包含 errorCode / errorMessage / userTip。</p>
 *
 * @param <T> 业务数据类型
 * @author library-backend
 */
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** HTTP 状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /** 错误码（5位，A/B/C+四位数字） */
    private String errorCode;

    /** 错误简短信息（面向开发排查） */
    private String errorMessage;

    /** 用户提示信息（面向终端用户） */
    private String userTip;

    /** 业务数据 */
    private T data;

    public ApiResponse() {
    }

    /**
     * 成功响应。
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    /**
     * 成功响应（无数据）。
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 错误响应。
     *
     * @param code        HTTP 状态码
     * @param errorCode   错误码
     * @param errorMessage 错误信息
     * @param userTip     用户提示
     * @param <T>         数据类型
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(Integer code, String errorCode, String errorMessage, String userTip) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        response.setUserTip(userTip);
        response.setData(null);
        return response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUserTip() {
        return userTip;
    }

    public void setUserTip(String userTip) {
        this.userTip = userTip;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{"
                + "code=" + code
                + ", message='" + message + '\''
                + ", errorCode='" + errorCode + '\''
                + ", errorMessage='" + errorMessage + '\''
                + ", userTip='" + userTip + '\''
                + ", data=" + data
                + '}';
    }
}

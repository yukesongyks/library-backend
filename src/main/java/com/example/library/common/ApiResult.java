package com.example.library.common;

/**
 * Unified API response wrapper.
 *
 * @param <T> the type of the data payload
 */
public class ApiResult<T> {

    private int code;
    private String message;
    private T data;

    public ApiResult() {
    }

    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a success response with code 0.
     *
     * @param data the response data
     * @param <T>  the data type
     * @return ApiResult with code=0, message="success"
     */
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(0, "success", data);
    }

    /**
     * Creates an error response with the given code and message.
     *
     * @param code    the error code
     * @param message the error message
     * @param <T>     the data type
     * @return ApiResult with the specified error code and data=null
     */
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
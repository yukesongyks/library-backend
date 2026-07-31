package com.antgroup.library.reader.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 修改读者请求（W09）。
 */
@Data
public class ReaderUpdateRequest {

    @Size(max = 50, message = "姓名长度不能超过50")
    private String name;

    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;

    @Size(max = 20, message = "读者类型长度不能超过20")
    private String readerType;

    @Size(max = 20, message = "状态长度不能超过20")
    private String status;
}

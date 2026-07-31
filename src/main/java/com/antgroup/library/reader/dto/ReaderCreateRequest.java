package com.antgroup.library.reader.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 新增读者请求（W08）。
 */
@Data
public class ReaderCreateRequest {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;

    @NotBlank(message = "读者类型不能为空")
    @Size(max = 20, message = "读者类型长度不能超过20")
    private String readerType;
}

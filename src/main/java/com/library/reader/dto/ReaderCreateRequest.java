package com.library.reader.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增读者请求。
 *
 * @author DTCoder
 */
@Data
public class ReaderCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 登录账号 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 初始密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 读者编号 */
    @NotBlank(message = "读者编号不能为空")
    private String readerNo;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 联系电话 */
    @NotBlank(message = "联系电话不能为空")
    private String phone;
}

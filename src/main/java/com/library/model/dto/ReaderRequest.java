package com.library.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 读者 Request - 请求参数
 *
 * @author library
 */
@Data
public class ReaderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 读者姓名 */
    @NotBlank(message = "读者姓名不能为空")
    @Size(max = 50, message = "读者姓名长度不能超过50")
    private String name;

    /** 联系电话 */
    @NotBlank(message = "联系电话不能为空")
    @Size(max = 20, message = "联系电话长度不能超过20")
    private String phone;

    /** 邮箱 */
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;
}

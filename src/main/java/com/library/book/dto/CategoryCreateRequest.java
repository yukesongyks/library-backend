package com.library.book.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增图书分类请求。
 *
 * @author DTCoder
 */
@Data
public class CategoryCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类名称 */
    @NotBlank(message = "分类名称不能为空")
    private String name;
}

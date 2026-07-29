package com.library.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 图书 Request - 请求参数
 *
 * @author library
 */
@Data
public class BookRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 书名 */
    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名长度不能超过200")
    private String title;

    /** 作者 */
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过100")
    private String author;

    /** ISBN */
    @NotBlank(message = "ISBN不能为空")
    @Size(max = 20, message = "ISBN长度不能超过20")
    private String isbn;

    /** 分类 */
    @NotBlank(message = "分类不能为空")
    @Size(max = 50, message = "分类长度不能超过50")
    private String category;

    /** 库存数量 */
    @NotNull(message = "库存数量不能为空")
    @PositiveOrZero(message = "库存数量不能为负数")
    private Integer stock;
}

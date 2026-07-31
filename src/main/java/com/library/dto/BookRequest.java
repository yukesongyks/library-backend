package com.library.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 图书新增/更新请求DTO
 *
 * @author library-team
 */
@Data
public class BookRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 书名
     */
    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名长度不能超过200")
    private String title;

    /**
     * 作者
     */
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过100")
    private String author;

    /**
     * ISBN编号
     */
    @NotBlank(message = "ISBN不能为空")
    @Pattern(regexp = "^\\d{10,13}$", message = "ISBN必须为10到13位数字")
    private String isbn;

    /**
     * 出版社
     */
    @Size(max = 200, message = "出版社长度不能超过200")
    private String publisher;

    /**
     * 库存数量
     */
    @NotNull(message = "库存数量不能为空")
    @PositiveOrZero(message = "库存数量不能为负数")
    private Integer stock;
}

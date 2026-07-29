package com.library.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图书 VO - 返回视图
 *
 * @author library
 */
@Data
public class BookVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 书名 */
    private String title;

    /** 作者 */
    private String author;

    /** ISBN */
    private String isbn;

    /** 分类 */
    private String category;

    /** 库存数量 */
    private Integer stock;

    /** 总库存数量 */
    private Integer totalStock;

    /** 借出数量 */
    private Integer borrowedCount;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}

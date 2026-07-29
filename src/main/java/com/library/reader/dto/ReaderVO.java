package com.library.reader.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 读者视图对象。
 *
 * @author DTCoder
 */
@Data
public class ReaderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 读者ID */
    private Long id;

    /** 关联用户ID */
    private Long userId;

    /** 读者编号 */
    private String readerNo;

    /** 联系电话（脱敏展示） */
    private String phone;

    /** 姓名 */
    private String name;
}

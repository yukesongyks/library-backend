package com.library.reader.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改读者请求。
 *
 * @author DTCoder
 */
@Data
public class ReaderUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 联系电话 */
    private String phone;

    /** 姓名 */
    private String name;
}

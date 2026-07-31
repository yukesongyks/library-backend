package com.antgroup.library.reader.dto;

import com.antgroup.library.common.response.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 读者分页查询请求（W06）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReaderQueryRequest extends PageQuery {

    private String name;
    private String phone;
}

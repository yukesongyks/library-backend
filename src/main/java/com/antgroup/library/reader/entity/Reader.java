package com.antgroup.library.reader.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 读者信息实体（对应表 reader）。
 */
@Data
public class Reader implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String phone;
    private String readerType;
    private String status;
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;
}

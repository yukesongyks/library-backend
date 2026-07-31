package com.antgroup.library.reader.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 读者返回视图（W06/W07）。手机号脱敏展示（设计文档 6.4.3.2）。
 */
@Data
public class ReaderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String phone;
    private String readerType;
    private String status;
}

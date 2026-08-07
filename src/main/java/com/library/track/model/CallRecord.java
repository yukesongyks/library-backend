package com.library.track.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 调用埋点记录实体（biz_call_record）
 */
@Data
public class CallRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String bizType;
    private String callerId;
    private String callerName;
    private String callerType;
    private String callerLevel;
    private String callerDept;
    private Long costMs;
    private String result;
    private Date gmtCreate;
    private Date gmtModified;
}

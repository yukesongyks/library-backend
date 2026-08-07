package com.library.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 调用人上下文信息（由统一登录拦截器/网关注入请求头）
 */
@Data
public class CallerContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 调用人ID */
    private String callerId;
    /** 调用人姓名 */
    private String callerName;
    /** 人员类型 */
    private String callerType;
    /** 人员层级 */
    private String callerLevel;
    /** 人员部门 */
    private String callerDept;
}

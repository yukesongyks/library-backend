package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * API 统一请求 DTO
 *
 * @author AI
 * @date 2026/08/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest {

    /** 用户ID */
    @NotBlank(message = "userId不能为空")
    private String userId;

    /** 人员类型：EMPLOYEE | CONTRACTOR | INTERN */
    @NotBlank(message = "userType不能为空")
    @Pattern(regexp = "^(EMPLOYEE|CONTRACTOR|INTERN)$", message = "userType必须为EMPLOYEE、CONTRACTOR或INTERN")
    private String userType;

    /** 人员层级 */
    private String level;

    /** 人员部门 */
    private String department;

    /** 哈希算法名称（如 SHA-256），哈希接口必填 */
    private String algorithm;

    /** 哈希算法输入（可选） */
    private String input;

    /** 冒泡排序数组（可选） */
    private List<Integer> array;
}

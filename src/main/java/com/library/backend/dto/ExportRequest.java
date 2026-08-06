package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 导出请求 DTO
 *
 * @author AI
 * @date 2026/08/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {

    /** 导出 tab 类型：hello | hash | bubble-sort */
    @NotBlank(message = "tab不能为空")
    private String tab;

    /** 过滤条件，可包含 startDate / endDate / apiName 等 */
    private Map<String, Object> filters;
}

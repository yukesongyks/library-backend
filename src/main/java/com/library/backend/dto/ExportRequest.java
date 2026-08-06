package com.library.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {
    /** 导出 tab 类型：hello | hash | bubble-sort */
    private String tab;
    /** 过滤条件，可包含 startDate / endDate / apiName 等 */
    private Map<String, Object> filters;
}

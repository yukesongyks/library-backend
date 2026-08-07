package com.antdigital.library.demo.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 导出服务接口。
 *
 * @author library-backend
 */
public interface ExportService {

    /**
     * 导出指定类型的结果为 CSV 文件。
     *
     * @param type     导出类型（helloworld / hash / bubble-sort）
     * @param input    功能输入参数（hash 传字符串，bubble-sort 传逗号分隔数字串，helloworld 可空）
     * @param response HTTP 响应
     */
    void export(String type, String input, HttpServletResponse response);
}

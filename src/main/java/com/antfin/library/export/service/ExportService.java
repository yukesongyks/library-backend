package com.antfin.library.export.service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 导出服务
 */
public interface ExportService {

    /**
     * 导出算法执行结果
     *
     * @param algorithmType 算法类型
     * @param numbers       冒泡排序用数字数组（逗号分隔字符串），其他算法可为空
     * @param inputText     哈希算法输入文本
     * @param algorithm     哈希算法名称
     * @param response      HTTP 响应
     */
    void exportAlgorithmResult(String algorithmType, String numbers, String inputText,
                                String algorithm, HttpServletResponse response);
}

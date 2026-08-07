package com.antdigital.library.demo.controller;

import com.antdigital.library.demo.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 导出 Controller。
 *
 * <p>支持导出各页面展示结果为 CSV 文件。</p>
 *
 * @author library-backend
 */
@RestController
@RequestMapping("/api/demo")
public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * 导出指定类型的结果。
     *
     * @param type  导出类型（helloworld / hash / bubble-sort）
     * @param input 功能输入参数（hash 传字符串，bubble-sort 传逗号分隔数字串，helloworld 可空）
     * @param response HTTP 响应
     */
    @GetMapping("/export")
    public void export(
            @RequestParam String type,
            @RequestParam(required = false) String input,
            HttpServletResponse response) {
        logger.info("收到导出请求, type: {}, input: {}", type, input);
        exportService.export(type, input, response);
    }
}

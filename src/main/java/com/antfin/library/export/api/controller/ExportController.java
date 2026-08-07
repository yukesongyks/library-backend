package com.antfin.library.export.api.controller;

import com.antfin.library.common.exception.BusinessException;
import com.antfin.library.export.service.ExportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 导出接口
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * W04 导出算法执行结果
     */
    @GetMapping("/algorithm-result")
    public void exportAlgorithmResult(
            @RequestParam(value = "algorithmType") String algorithmType,
            @RequestParam(value = "numbers", required = false) String numbers,
            @RequestParam(value = "inputText", required = false) String inputText,
            @RequestParam(value = "algorithm", required = false) String algorithm,
            HttpServletResponse response) {
        if (algorithmType == null || algorithmType.trim().isEmpty()) {
            throw new BusinessException("PARAM_ERROR", "algorithmType 不能为空");
        }
        exportService.exportAlgorithmResult(algorithmType, numbers, inputText, algorithm, response);
    }
}

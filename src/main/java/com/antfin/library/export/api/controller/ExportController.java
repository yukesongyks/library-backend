package com.antfin.library.export.api.controller;

import com.antfin.library.export.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ExportService exportService;

    /**
     * W04 导出算法执行结果
     */
    @GetMapping("/algorithm-result")
    public void exportAlgorithmResult(
            @RequestParam(value = "algorithmType", required = false) String algorithmType,
            @RequestParam(value = "numbers", required = false) String numbers,
            @RequestParam(value = "inputText", required = false) String inputText,
            @RequestParam(value = "algorithm", required = false) String algorithm,
            HttpServletResponse response) {
        exportService.exportAlgorithmResult(algorithmType, numbers, inputText, algorithm, response);
    }
}

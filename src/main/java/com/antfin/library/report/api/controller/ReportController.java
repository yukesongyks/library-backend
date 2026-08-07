package com.antfin.library.report.api.controller;

import com.antfin.library.common.model.Result;
import com.antfin.library.report.model.request.ReportRequest;
import com.antfin.library.report.model.vo.AlgoCallStatsVO;
import com.antfin.library.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 报表接口
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * W05 算法调用统计报表查询
     */
    @PostMapping("/algorithm-call-stats")
    public Result<AlgoCallStatsVO> queryCallStats(@Valid @RequestBody ReportRequest request) {
        return Result.success(reportService.queryCallStats(request));
    }
}

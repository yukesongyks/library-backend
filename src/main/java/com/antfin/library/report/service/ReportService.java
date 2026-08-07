package com.antfin.library.report.service;

import com.antfin.library.report.model.request.ReportRequest;
import com.antfin.library.report.model.vo.AlgoCallStatsVO;

/**
 * 报表服务
 */
public interface ReportService {

    /**
     * 查询算法调用统计报表
     *
     * @param request 报表查询请求
     * @return 统计数据
     */
    AlgoCallStatsVO queryCallStats(ReportRequest request);
}

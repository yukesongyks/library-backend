package com.library.cost.service;

import com.library.cost.dto.AnalysisQuery;
import com.library.cost.dto.AnalysisResponse;
import com.library.cost.dto.CostAnalysisItem;
import com.library.cost.dto.CostSummaryDTO;

import java.util.List;

public interface CostService {

    CostSummaryDTO summary(String year);

    AnalysisResponse analysis(AnalysisQuery query);

    List<CostAnalysisItem> queryItems(AnalysisQuery query);
}

package com.library.cost.dto;

import java.util.List;

public record AnalysisResponse(List<CostAnalysisItem> records, long total) {
}

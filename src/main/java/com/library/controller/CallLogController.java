package com.library.controller;

import com.library.dto.CallStatRow;
import com.library.repository.CallLogRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@RestController
@RequestMapping("/api/stats")
public class CallLogController {
    @Autowired
    private CallLogRepository callLogRepository;

    @GetMapping
    public Map<String, List<CallStatRow>> stats() {
        List<Object[]> rows = callLogRepository.findAllByDimensions();
        List<CallStatRow> flat = new ArrayList<>();
        for (Object[] r : rows) {
            flat.add(new CallStatRow(
                (String) r[0], (String) r[1], ((Number) r[2]).longValue()
            ));
        }
        Map<String, List<CallStatRow>> grouped = new LinkedHashMap<>();
        grouped.put("userType", new ArrayList<>());
        grouped.put("userLevel", new ArrayList<>());
        grouped.put("department", new ArrayList<>());
        for (CallStatRow row : flat) {
            grouped.get(row.dimension()).add(row);
        }
        return grouped;
    }
}

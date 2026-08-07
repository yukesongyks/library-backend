package com.library.controller;

import com.library.dto.CallStatRow;
import com.library.repository.CallLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/stats")
public class CallLogController {
    private final CallLogRepository callLogRepository;

    // M2: 改为构造注入，省略 @Autowired
    public CallLogController(CallLogRepository callLogRepository) {
        this.callLogRepository = callLogRepository;
    }

    @GetMapping
    public Map<String, List<CallStatRow>> stats() {
        List<Object[]> rows = callLogRepository.findAllByDimensions();
        Map<String, List<CallStatRow>> grouped = new LinkedHashMap<>();
        grouped.put("userType", new ArrayList<>());
        grouped.put("userLevel", new ArrayList<>());
        grouped.put("department", new ArrayList<>());
        for (Object[] r : rows) {
            String dimension = (String) r[0];
            // M3: NULL 值防御
            String value = r[1] != null ? (String) r[1] : "UNKNOWN";
            long count = ((Number) r[2]).longValue();
            CallStatRow row = new CallStatRow(dimension, value, count);
            // M3: 用 computeIfAbsent 防御未预期 dimension 导致 NPE
            grouped.computeIfAbsent(dimension, k -> new ArrayList<>()).add(row);
        }
        return grouped;
    }
}

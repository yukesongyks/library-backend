package com.library.backend.controller;

import com.library.backend.dto.AlgorithmResponse;
import com.library.backend.dto.BubbleSortRequest;
import com.library.backend.dto.HashRequest;
import com.library.backend.model.CallLog;
import com.library.backend.repository.CallLogRepository;
import com.library.backend.service.AlgorithmService;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 算法接口控制器。
 *
 * <p>提供三个算法接口 + 一个导出接口（spec {@code algorithms.md} + {@code export.md}）：
 * <ul>
 *   <li>{@code GET /api/algorithms/helloworld} → {@code {"result":"Hello, World!"}}</li>
 *   <li>{@code POST /api/algorithms/hash} → {@code {"result":"<hex>","algorithm":"<name>"}}</li>
 *   <li>{@code POST /api/algorithms/bubble-sort} → {@code {"result":[...],"input":[...]}}</li>
 *   <li>{@code GET /api/algorithms/export?type=} → CSV 文件流</li>
 * </ul>
 * 调用人标识通过 {@code X-User-Id} header 传入，仅用于埋点切面关联用户维度，
 * 不参与算法业务逻辑。</p>
 */
@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {

    /** 导出支持的 API 类型（spec export.md） */
    private static final Set<String> EXPORT_TYPES = Set.of("helloworld", "hash", "bubble-sort");

    private static final DateTimeFormatter CSV_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String CSV_HEADER =
            "id,apiType,callerUsername,personnelType,personnelLevel,department,calledAt,requestSummary,responseData";

    private final AlgorithmService algorithmService;
    private final CallLogRepository callLogRepository;

    public AlgorithmController(AlgorithmService algorithmService, CallLogRepository callLogRepository) {
        this.algorithmService = algorithmService;
        this.callLogRepository = callLogRepository;
    }

    /**
     * HelloWorld 接口。
     * <p>{@code X-User-Id} 仅用于埋点，接口本身不读它；留在此仅声明用途。
     */
    @GetMapping("/helloworld")
    public AlgorithmResponse<String> helloworld(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return new AlgorithmResponse<>(algorithmService.helloworld());
    }

    /**
     * 哈希算法接口。
     */
    @PostMapping("/hash")
    public AlgorithmResponse<String> hash(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody HashRequest request) {
        String result = algorithmService.hash(request.getText(), request.getAlgorithm());
        return new AlgorithmResponse<>(result, request.getAlgorithm());
    }

    /**
     * 冒泡排序接口。
     */
    @PostMapping("/bubble-sort")
    public AlgorithmResponse<List<Integer>> bubbleSort(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody BubbleSortRequest request) {
        List<Integer> input = request.getNumbers() == null ? List.of() : request.getNumbers();
        List<Integer> result = algorithmService.bubbleSort(input);
        return new AlgorithmResponse<>(result, new ArrayList<>(input));
    }

    /**
     * 按 API 类型导出调用记录 CSV。
     *
     * <p>spec export.md：
     * <ul>
     *   <li>type 为空 → 400 {@code {"error":"type parameter is required"}}</li>
     *   <li>type 无效 → 400 {@code {"error":"Invalid export type: <type>. Supported: helloworld, hash, bubble-sort"}}</li>
     *   <li>无数据 → 200 + 仅表头行</li>
     * </ul>
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(value = "type", required = false) String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type parameter is required");
        }
        if (!EXPORT_TYPES.contains(type)) {
            throw new IllegalArgumentException(
                    "Invalid export type: " + type + ". Supported: helloworld, hash, bubble-sort");
        }

        List<CallLog> logs = callLogRepository.findByApiTypeOrderByIdAsc(type);
        StringBuilder csv = new StringBuilder(CSV_HEADER).append('\n');
        for (CallLog log : logs) {
            csv.append(safe(log.getId())).append(',')
               .append(safe(log.getApiType())).append(',')
               .append(safe(log.getCallerUsername())).append(',')
               .append(safe(log.getPersonnelType())).append(',')
               .append(safe(log.getPersonnelLevel())).append(',')
               .append(safe(log.getDepartment())).append(',')
               .append(log.getCalledAt() == null ? "" : log.getCalledAt().format(CSV_DATE_FMT)).append(',')
               .append(safe(log.getRequestSummary())).append(',')
               .append(safe(log.getResponseData())).append('\n');
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + type + "-export.csv\"");
        return ResponseEntity.ok().headers(headers).body(csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}

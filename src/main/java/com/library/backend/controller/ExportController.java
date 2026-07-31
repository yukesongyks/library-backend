package com.library.backend.controller;

import com.library.backend.model.dto.BubbleSortResponse;
import com.library.backend.model.dto.HashResponse;
import com.library.backend.service.DemoService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CSV export endpoint (design spec §4.5, §5.2.4).
 *
 * <p>{@code GET /api/demo/export?tab=helloworld|hash|bubble-sort&format=csv(optional)}.
 *
 * <ul>
 *   <li>Returns {@code Content-Type: text/csv; charset=UTF-8} +
 *       {@code Content-Disposition: attachment; filename=<tab>-result.csv}.</li>
 *   <li>Writes a UTF-8 BOM (EF BB BF) first for Excel Chinese support.</li>
 *   <li>Per-tab columns: helloworld → "message"; hash → original,algorithm,digest;
 *       bubble-sort → index,value.</li>
 *   <li>Backward compat: an unrecognized {@code format} value falls back to csv
 *       and sets {@code X-Export-Fallback: csv}.</li>
 *   <li>Illegal {@code tab} (via {@code @Pattern}) raises a
 *       {@link jakarta.validation.ConstraintViolationException} handled by
 *       {@link com.library.backend.common.GlobalExceptionHandler}, which returns
 *       {@code Result.error(40001, "非法 tab 参数")} as JSON (NOT entering the
 *       download stream) so the frontend can detect the non-CSV Content-Type.</li>
 *   <li>CSV {@link IOException} → HTTP 500 + Result.error(50000, "导出失败，请稍后重试")
 *       (handled by the global advice).</li>
 *   <li>Export uses a sensible default sample when there is no session input:
 *       hash a fixed sample text ({@link DemoService#DEFAULT_HASH_SAMPLE_TEXT});
 *       sort a fixed sample array ({@link DemoService#DEFAULT_BUBBLE_SORT_SAMPLE}).
 *       So export always returns content.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/demo")
@Validated
public class ExportController {

    private final DemoService demoService;

    public ExportController(DemoService demoService) {
        this.demoService = demoService;
    }

    /** Legal tab values, also reused for the CSV filename. */
    public static final String TAB_HELLO_WORLD = "helloworld";
    public static final String TAB_HASH = "hash";
    public static final String TAB_BUBBLE_SORT = "bubble-sort";

    private static final String CONTENT_TYPE = "text/csv; charset=UTF-8";
    private static final byte[] UTF8_BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    /**
     * Export endpoint.
     *
     * @param tab    one of helloworld|hash|bubble-sort (validated via @Pattern)
     * @param format optional, defaults to csv; unknown values fall back to csv
     * @param response servlet response for stream writing
     * @throws IOException if writing the CSV stream fails (mapped to 500 + 50000)
     */
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false)
            @NotNull(message = "非法 tab 参数")
            @Pattern(regexp = "helloworld|hash|bubble-sort", message = "非法 tab 参数")
            String tab,
            @RequestParam(required = false) String format,
            HttpServletResponse response) throws IOException {

        boolean fallback = format != null && !"csv".equalsIgnoreCase(format);
        String effectiveFormat = fallback ? "csv" : "csv";

        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition",
                "attachment; filename=" + tab + "-result.csv");
        if (fallback) {
            response.setHeader("X-Export-Fallback", effectiveFormat);
        }

        // Build the full CSV body in memory first so an IOException during
        // generation can still be mapped to a 500 + JSON error (§5.2.4) before
        // the response stream is committed.
        byte[] csvBody = buildCsv(tab).getBytes(StandardCharsets.UTF_8);

        try (OutputStream out = response.getOutputStream()) {
            out.write(UTF8_BOM);
            out.write(csvBody);
            out.flush();
        }
    }

    /**
     * Build the CSV body (without BOM) for the given tab using default sample
     * data (export has no session input).
     */
    private String buildCsv(String tab) {
        StringBuilder sb = new StringBuilder();
        switch (tab) {
            case TAB_HELLO_WORLD -> {
                sb.append("message\r\n");
                sb.append(csvEscape(demoService.helloWorldMessage())).append("\r\n");
            }
            case TAB_HASH -> {
                HashResponse h = demoService.exportHashSample();
                sb.append("original,algorithm,digest\r\n");
                sb.append(csvEscape(h.original())).append(',')
                        .append(csvEscape(h.algorithm())).append(',')
                        .append(csvEscape(h.digest())).append("\r\n");
            }
            case TAB_BUBBLE_SORT -> {
                BubbleSortResponse b = demoService.exportBubbleSortSample();
                sb.append("index,value\r\n");
                List<Integer> sorted = b.sorted();
                for (int i = 0; i < sorted.size(); i++) {
                    sb.append(i).append(',').append(sorted.get(i)).append("\r\n");
                }
            }
            default -> throw new IllegalArgumentException("非法 tab 参数");
        }
        return sb.toString();
    }

    /**
     * Minimal RFC 4180 CSV escaping: wrap in double quotes when the value
     * contains a comma, quote, CR or LF; double any internal double quotes.
     */
    private static String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuoting = value.indexOf(',') >= 0
                || value.indexOf('"') >= 0
                || value.indexOf('\r') >= 0
                || value.indexOf('\n') >= 0;
        if (!needsQuoting) {
            return value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}

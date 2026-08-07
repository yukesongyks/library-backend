package com.library.controller;

import com.library.dto.AlgoResult;
import com.library.service.AlgoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {
    private final AlgoService algoService;

    public ExportController(AlgoService algoService) {
        this.algoService = algoService;
    }

    @GetMapping("/{apiName}")
    public void export(@PathVariable String apiName,
                       @RequestParam(defaultValue = "hello") String input,
                       HttpServletResponse response) throws Exception {
        // B3: 校验 apiName 白名单，防止响应头注入
        if (!List.of("helloworld", "hash", "bubblesort").contains(apiName)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().println("error, unknown api: " + apiName);
            return;
        }

        response.setContentType("text/csv; charset=UTF-8");
        // B3: filename 使用 RFC 5987 编码防止响应头注入
        String encodedName = URLEncoder.encode(apiName + ".csv", StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"" + apiName + ".csv\"; filename*=UTF-8''" + encodedName);
        PrintWriter writer = response.getWriter();
        writer.println("apiName,input,output,durationMs");
        switch (apiName) {
            case "helloworld" -> {
                AlgoResult r = algoService.helloworld();
                writer.println(csvRow(r.apiName(), r.input(), r.output(), r.durationMs()));
            }
            case "hash" -> {
                AlgoResult r = algoService.hash(input);
                writer.println(csvRow(r.apiName(), r.input(), r.output(), r.durationMs()));
            }
            case "bubblesort" -> {
                AlgoResult r = algoService.bubblesort(input);
                writer.println(csvRow(r.apiName(), r.input(), r.output(), r.durationMs()));
            }
            default -> {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writer.println("error,unknown api: " + apiName);
            }
        }
        writer.flush();
    }

    /**
     * B3: 统一 CSV 字段转义——含逗号、引号、换行时用双引号包裹，内部引号双写。
     * 公式前缀(=, +, -, @, TAB, CR) 前缀单引号防止 Excel 公式注入。
     */
    private String csvEscape(Object field) {
        String s = field == null ? "" : field.toString();
        // 公式注入防护
        if (!s.isEmpty()) {
            char first = s.charAt(0);
            if (first == '=' || first == '+' || first == '-' || first == '@' || first == '\t' || first == '\r') {
                s = "'" + s;
            }
        }
        // 包含特殊字符则用双引号包裹，内部引号双写
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private String csvRow(String apiName, Object input, Object output, long durationMs) {
        return csvEscape(apiName) + "," + csvEscape(input) + "," + csvEscape(output) + "," + durationMs;
    }
}

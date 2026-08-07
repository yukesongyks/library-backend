package com.library.controller;

import com.library.dto.AlgoResult;
import com.library.service.AlgoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;

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
                       @RequestParam(defaultValue = "5,3,8,1,9,2,7") String sortInput,
                       HttpServletResponse response) throws Exception {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"" + apiName + ".csv\"");
        PrintWriter writer = response.getWriter();
        switch (apiName) {
            case "helloworld" -> {
                AlgoResult r = algoService.helloworld();
                writer.println("apiName,input,output,durationMs");
                writer.println(r.apiName() + "," + r.input() + "," + r.output() + "," + r.durationMs());
            }
            case "hash" -> {
                AlgoResult r = algoService.hash(input);
                writer.println("apiName,input,output,durationMs");
                writer.println(r.apiName() + "," + r.input() + "," + r.output() + "," + r.durationMs());
            }
            case "bubblesort" -> {
                AlgoResult r = algoService.bubblesort(sortInput);
                writer.println("apiName,input,output,durationMs");
                writer.println(r.apiName() + ",\"" + r.input() + "\",\"" + r.output() + "\"," + r.durationMs());
            }
            default -> {
                response.setStatus(404);
                writer.println("error,unknown api: " + apiName);
            }
        }
        writer.flush();
    }
}

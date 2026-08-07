package com.library.export.controller;

import com.library.common.context.RequestContextHelper;
import com.library.common.enums.BizTypeEnum;
import com.library.common.enums.CallResultEnum;
import com.library.common.exception.BizException;
import com.library.common.model.CallerContext;
import com.library.export.service.ExportService;
import com.library.track.model.CallRecord;
import com.library.track.service.TrackService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 导出接口（W04）
 */
@RestController
@RequestMapping("/api/demo")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private TrackService trackService;

    @Autowired
    private RequestContextHelper requestContextHelper;

    @Value("${demo.track.enabled:true}")
    private boolean trackEnabled;

    /**
     * W04 导出结果
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(value = "bizType", required = false) String bizType,
            @RequestParam(value = "numbers", required = false) String numbers,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "algorithm", required = false) String algorithm,
            HttpServletRequest httpRequest) {

        long start = System.currentTimeMillis();
        CallResultEnum callResult = CallResultEnum.SUCCESS;
        try {
            Map<String, String> params = new HashMap<>();
            params.put("numbers", numbers);
            params.put("text", text);
            params.put("algorithm", algorithm);

            byte[] fileData = exportService.export(bizType, params);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String filename = "demo_" + bizType + ".xlsx";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(fileData.length);

            return ResponseEntity.ok().headers(headers).body(fileData);
        } catch (Exception e) {
            callResult = CallResultEnum.FAIL;
            if (e instanceof BizException) {
                throw (BizException) e;
            }
            throw new BizException("EXPORT_003", "导出异常: " + e.getMessage());
        } finally {
            trackExportCall(httpRequest, bizType, System.currentTimeMillis() - start, callResult);
        }
    }

    private void trackExportCall(HttpServletRequest httpRequest, String bizType,
                                  long costMs, CallResultEnum result) {
        if (!trackEnabled || bizType == null) {
            return;
        }
        BizTypeEnum bizEnum = BizTypeEnum.fromCode(bizType);
        if (bizEnum == null) {
            return;
        }
        try {
            CallerContext ctx = requestContextHelper.getCallerContext(httpRequest);
            CallRecord record = new CallRecord();
            record.setBizType(bizEnum.getCode());
            record.setCallerId(ctx.getCallerId());
            record.setCallerName(ctx.getCallerName());
            record.setCallerType(ctx.getCallerType());
            record.setCallerLevel(ctx.getCallerLevel());
            record.setCallerDept(ctx.getCallerDept());
            record.setCostMs(costMs);
            record.setResult(result.getCode());
            trackService.recordCall(record);
        } catch (Exception e) {
            LoggerFactory.getLogger(ExportController.class)
                    .warn("导出埋点记录失败(降级忽略): bizType={}, error={}", bizType, e.getMessage());
        }
    }
}

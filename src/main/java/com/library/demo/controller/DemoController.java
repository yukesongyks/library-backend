package com.library.demo.controller;

import com.library.common.context.RequestContextHelper;
import com.library.common.enums.BizTypeEnum;
import com.library.common.enums.CallResultEnum;
import com.library.common.exception.BizException;
import com.library.common.model.ApiResponse;
import com.library.common.model.CallerContext;
import com.library.demo.model.DemoResult;
import com.library.demo.model.HashRequest;
import com.library.demo.model.HashResult;
import com.library.demo.model.SortRequest;
import com.library.demo.model.SortResult;
import com.library.demo.service.DemoService;
import com.library.track.model.CallRecord;
import com.library.track.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * demo 演示接口（W01/W02/W03）
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @Autowired
    private TrackService trackService;

    @Autowired
    private RequestContextHelper requestContextHelper;

    @Value("${demo.track.enabled:true}")
    private boolean trackEnabled;

    @Value("${demo.helloworld.enabled:true}")
    private boolean helloworldEnabled;

    @Value("${demo.hash.enabled:true}")
    private boolean hashEnabled;

    @Value("${demo.bubblesort.enabled:true}")
    private boolean bubbleSortEnabled;

    /**
     * W01 helloworld
     */
    @PostMapping("/helloworld")
    public ApiResponse<DemoResult> helloworld(HttpServletRequest httpRequest) {
        if (!helloworldEnabled) {
            throw new BizException("DEMO_001", "helloworld接口已关闭");
        }
        long start = System.currentTimeMillis();
        CallResultEnum callResult = CallResultEnum.SUCCESS;
        try {
            DemoResult data = demoService.helloworld();
            return ApiResponse.success(data);
        } catch (Exception e) {
            callResult = CallResultEnum.FAIL;
            if (e instanceof BizException) {
                throw e;
            }
            throw new BizException("DEMO_001", "服务处理异常: " + e.getMessage());
        } finally {
            trackCall(httpRequest, BizTypeEnum.HELLOWORLD, System.currentTimeMillis() - start, callResult);
        }
    }

    /**
     * W02 哈希算法
     */
    @PostMapping("/hash")
    public ApiResponse<HashResult> hash(@RequestBody HashRequest req, HttpServletRequest httpRequest) {
        if (!hashEnabled) {
            throw new BizException("DEMO_001", "哈希接口已关闭");
        }
        long start = System.currentTimeMillis();
        CallResultEnum callResult = CallResultEnum.SUCCESS;
        try {
            HashResult data = demoService.hash(req);
            return ApiResponse.success(data);
        } catch (Exception e) {
            callResult = CallResultEnum.FAIL;
            if (e instanceof BizException) {
                throw e;
            }
            throw new BizException("DEMO_001", "服务处理异常: " + e.getMessage());
        } finally {
            trackCall(httpRequest, BizTypeEnum.HASH, System.currentTimeMillis() - start, callResult);
        }
    }

    /**
     * W03 冒泡排序
     */
    @PostMapping("/bubble-sort")
    public ApiResponse<SortResult> bubbleSort(@RequestBody SortRequest req, HttpServletRequest httpRequest) {
        if (!bubbleSortEnabled) {
            throw new BizException("DEMO_001", "冒泡排序接口已关闭");
        }
        long start = System.currentTimeMillis();
        CallResultEnum callResult = CallResultEnum.SUCCESS;
        try {
            SortResult data = demoService.bubbleSort(req);
            return ApiResponse.success(data);
        } catch (Exception e) {
            callResult = CallResultEnum.FAIL;
            if (e instanceof BizException) {
                throw e;
            }
            throw new BizException("DEMO_001", "服务处理异常: " + e.getMessage());
        } finally {
            trackCall(httpRequest, BizTypeEnum.BUBBLE_SORT, System.currentTimeMillis() - start, callResult);
        }
    }

    /**
     * 埋点调用（降级兜底：失败仅记日志不阻断主流程）
     */
    private void trackCall(HttpServletRequest httpRequest, BizTypeEnum bizType, long costMs, CallResultEnum result) {
        if (!trackEnabled) {
            return;
        }
        try {
            CallerContext ctx = requestContextHelper.getCallerContext(httpRequest);
            CallRecord record = new CallRecord();
            record.setBizType(bizType.getCode());
            record.setCallerId(ctx.getCallerId());
            record.setCallerName(ctx.getCallerName());
            record.setCallerType(ctx.getCallerType());
            record.setCallerLevel(ctx.getCallerLevel());
            record.setCallerDept(ctx.getCallerDept());
            record.setCostMs(costMs);
            record.setResult(result.getCode());
            trackService.recordCall(record);
        } catch (Exception e) {
            // 降级：埋点失败仅记日志
            org.slf4j.LoggerFactory.getLogger(DemoController.class)
                    .warn("埋点记录失败(降级忽略): bizType={}, error={}", bizType.getCode(), e.getMessage());
        }
    }
}

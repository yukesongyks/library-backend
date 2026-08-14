package com.library.aspect;

import com.library.entity.ApiMetrics;
import com.library.mapper.ApiMetricsMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Aspect
@Component
public class MetricsAspect {

    private static final Logger log = LoggerFactory.getLogger(MetricsAspect.class);
    private final ApiMetricsMapper apiMetricsMapper;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public MetricsAspect(ApiMetricsMapper apiMetricsMapper) {
        this.apiMetricsMapper = apiMetricsMapper;
    }

    @Around("execution(* com.library.controller.*.*(..))")
    public Object recordMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean success = true;
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            success = false;
            throw t;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            executor.submit(() -> {
                try {
                    ApiMetrics metrics = buildMetrics(joinPoint, duration, success);
                    apiMetricsMapper.insert(metrics);
                } catch (Exception e) {
                    log.error("埋点写入失败", e);
                }
            });
        }
    }

    private ApiMetrics buildMetrics(ProceedingJoinPoint joinPoint, long duration, boolean success) {
        ApiMetrics m = new ApiMetrics();
        m.setApiPath(extractApiPath(joinPoint));
        m.setCallTime(LocalDateTime.now());
        m.setDurationMs((int) duration);
        m.setSuccess(success ? 1 : 0);

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                m.setCallerId(getHeader(req, "X-User-Id", "anonymous"));
                m.setCallerName(getHeader(req, "X-User-Name", "anonymous"));
                m.setCallerType(getHeader(req, "X-User-Type", "unknown"));
                m.setCallerLevel(getHeader(req, "X-User-Level", "unknown"));
                m.setCallerDept(getHeader(req, "X-User-Dept", "unknown"));
            }
        } catch (Exception e) {
            log.warn("提取调用人信息失败", e);
        }
        return m;
    }

    private String extractApiPath(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        return "/api/" + className.substring(className.lastIndexOf('.') + 1).replace("Controller", "").toLowerCase()
                + "/" + methodName;
    }

    private String getHeader(HttpServletRequest req, String name, String defaultVal) {
        String val = req.getHeader(name);
        return val != null && !val.isEmpty() ? val : defaultVal;
    }
}
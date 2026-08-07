package com.antdigital.library.track.aspect;

import com.antdigital.library.track.service.TrackService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 埋点切面，拦截 demo 模块接口调用并记录调用信息。
 *
 * <p>调用人信息从请求头中获取（模拟身份认证场景）：
 * X-User-Id / X-User-Name / X-User-Type / X-User-Level / X-User-Department</p>
 *
 * @author library-backend
 */
@Aspect
@Component
public class TrackAspect {

    private static final Logger logger = LoggerFactory.getLogger(TrackAspect.class);

    private final TrackService trackService;

    public TrackAspect(TrackService trackService) {
        this.trackService = trackService;
    }

    /**
     * 环绕通知：拦截 demo 模块所有 Controller 方法。
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("execution(* com.antdigital.library.demo.controller..*.*(..))")
    public Object trackDemoCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String apiPath = "";
        String userId = "anonymous";
        String userName = "匿名用户";
        String userType = "访客";
        String userLevel = "L0";
        String userDepartment = "未知";

        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                apiPath = request.getRequestURI();

                userId = getHeaderOrDefault(request, "X-User-Id", "anonymous");
                userName = getHeaderOrDefault(request, "X-User-Name", "匿名用户");
                userType = getHeaderOrDefault(request, "X-User-Type", "访客");
                userLevel = getHeaderOrDefault(request, "X-User-Level", "L0");
                userDepartment = getHeaderOrDefault(request, "X-User-Department", "未知");
            }
        } catch (Exception e) {
            logger.warn("获取请求信息失败, errorMessage: {}", e.getMessage());
        }

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 异步保存埋点（此处同步保存，避免吞异常；生产环境可改为异步队列）
        try {
            trackService.saveTrackRecord(apiPath, userId, userName, userType, userLevel, userDepartment);
        } catch (Exception e) {
            // 埋点失败不影响业务流程
            logger.error("埋点保存失败, apiPath: {}, userId: {}, errorMessage: {}",
                    apiPath, userId, e.getMessage(), e);
        }

        return result;
    }

    /**
     * 从请求头获取值，为空时返回默认值。
     *
     * @param request      HTTP 请求
     * @param headerName   请求头名称
     * @param defaultValue 默认值
     * @return 请求头值或默认值
     */
    private String getHeaderOrDefault(HttpServletRequest request, String headerName, String defaultValue) {
        String value = request.getHeader(headerName);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}

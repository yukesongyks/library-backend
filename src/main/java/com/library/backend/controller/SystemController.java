package com.library.backend.controller;

import com.library.backend.vo.ApiResponse;
import com.library.backend.vo.HealthVO;
import com.library.backend.vo.SystemInfoVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 系统信息控制器
 * <p>
 * 提供系统身份查询与健康检查端点。
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(ZoneId.of("UTC"));

    private final Instant startTime = Instant.now();

    @Value("${app.name:library-backend}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.description:图书管理系统后端}")
    private String appDescription;

    @Value("${app.tech-stack:Spring Boot / Java}")
    private String appTechStack;

    @Value("${app.model:DTCoder - 蚂蚁数科研发 AI 编程智能体}")
    private String appModel;

    /**
     * 系统信息查询
     *
     * @return 系统元信息
     */
    @GetMapping("/info")
    public ApiResponse<SystemInfoVO> info() {
        SystemInfoVO vo = new SystemInfoVO(
                appName,
                appVersion,
                appDescription,
                appTechStack,
                appModel,
                ISO_FORMATTER.format(Instant.now())
        );
        return ApiResponse.success(vo);
    }

    /**
     * 健康检查
     *
     * @return 服务运行状态
     */
    @GetMapping("/health")
    public ApiResponse<HealthVO> health() {
        Duration uptime = Duration.between(startTime, Instant.now());
        String uptimeStr = formatUptime(uptime);

        HealthVO vo = new HealthVO("UP", uptimeStr, "OK");
        return ApiResponse.success(vo);
    }

    /**
     * 格式化运行时长
     */
    private String formatUptime(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%dh %dm", hours, minutes);
    }
}
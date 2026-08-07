package com.antfin.library.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 埋点异步执行配置
 */
@Configuration
@EnableAsync
public class TrackAsyncConfig {

    private static final Logger log = LoggerFactory.getLogger(TrackAsyncConfig.class);

    /**
     * 埋点专用线程池
     * <p>
     * A08/G5 资源隔离：队列满时丢弃埋点任务并记录告警日志，
     * 绝不退化为调用线程同步执行（即不使用 CallerRunsPolicy），
     * 确保埋点链路不影响算法接口主链路。
     */
    @Bean("trackExecutor")
    public Executor trackExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(2000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("track-async-");
        // A08/G5: 队列满时丢弃埋点 + 记录告警，不阻塞主链路
        executor.setRejectedExecutionHandler(trackDiscardHandler());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(5);
        executor.initialize();
        return executor;
    }

    /**
     * 埋点拒绝策略：丢弃任务并记录告警日志
     * <p>
     * 符合 spec A08「队列满则丢弃埋点并记日志告警，不影响算法接口」。
     */
    private RejectedExecutionHandler trackDiscardHandler() {
        return (Runnable runnable, ThreadPoolExecutor executor) -> {
            // 丢弃埋点任务，仅记录告警，不抛异常、不阻塞调用线程
            log.warn("埋点线程池队列已满，丢弃埋点任务: poolSize={}, queueSize={}",
                    executor.getPoolSize(), executor.getQueue().size());
        };
    }
}

package com.library.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 图书管理系统后端启动入口。
 *
 * <p>{@code @EnableAspectJAutoProxy} 启用 AspectJ 自动代理，使 {@code CallLogAspect}
 * 能拦截 {@code AlgorithmController} 方法完成调用埋点。</p>
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class LibraryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryBackendApplication.class, args);
    }
}

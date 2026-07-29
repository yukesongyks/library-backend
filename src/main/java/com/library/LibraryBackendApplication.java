package com.library;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 图书管理系统后端启动类。
 *
 * @author DTCoder
 */
@SpringBootApplication
@MapperScan("com.library.**.mapper")
@EnableTransactionManagement
public class LibraryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryBackendApplication.class, args);
    }
}

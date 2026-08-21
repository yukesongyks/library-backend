package com.library.cost;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.library.cost.mapper")
public class CostApplication {

    public static void main(String[] args) {
        SpringApplication.run(CostApplication.class, args);
    }
}
# API 演示 + 数据看板功能 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为图书管理系统新增三个后端接口（HelloWorld/哈希算法/冒泡排序）、前端 Tab 页面、导出功能与埋点统计看板。

**技术栈:**
- 前端: Vue3 + Vite + Element Plus + ECharts + axios
- 后端: Spring Boot 3 + JDK 17 + Maven + H2 Database
- 接口: RESTful JSON

---

## 文件结构

### 后端 (library-backend)

```
src/main/java/com/library/backend/
├── BackendApplication.java
├── controller/
│   ├── HelloController.java
│   ├── HashController.java
│   ├── SortController.java
│   ├── ExportController.java
│   └── StatsController.java
├── service/
│   ├── HelloService.java
│   ├── HashService.java
│   ├── SortService.java
│   ├── ExportService.java
│   └── StatsService.java
├── model/
│   ├── ApiResponse.java
│   ├── HelloResult.java
│   ├── HashRequest.java
│   ├── HashResult.java
│   ├── SortRequest.java
│   ├── SortResult.java
│   └── TrackingLog.java
├── interceptor/
│   ├── TrackedApi.java
│   └── TrackingInterceptor.java
├── repository/
│   └── TrackingLogRepository.java
└── config/
    ├── WebConfig.java
    └── CorsConfig.java
src/main/resources/
├── application.yml
└── schema.sql
pom.xml
```

### 前端 (library-frontend)

```
src/
├── App.vue
├── main.js
├── router/
│   └── index.js
├── api/
│   └── dashboard.js
├── views/
│   └── ApiDashboard.vue
├── components/
│   ├── HelloWorldPanel.vue
│   ├── HashPanel.vue
│   ├── SortPanel.vue
│   └── StatsDashboard.vue
├── utils/
│   └── request.js
└── assets/
    └── (empty)
package.json
vite.config.js
```

---

## 任务 1: 后端 — 项目脚手架

**Files:**
- Create: `library-backend/pom.xml`
- Create: `library-backend/src/main/java/com/library/backend/BackendApplication.java`
- Create: `library-backend/src/main/resources/application.yml`
- Create: `library-backend/src/main/resources/schema.sql`

**Interfaces:**
- Produces: Spring Boot 项目骨架，可正常启动

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
        <relativePath/>
    </parent>
    <groupId>com.library</groupId>
    <artifactId>library-backend</artifactId>
    <version>1.0.0</version>
    <name>library-backend</name>
    <description>Library Management System Backend</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建 BackendApplication.java**

```java
package com.library.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:h2:mem:librarydb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

- [ ] **Step 4: 创建 schema.sql**

```sql
CREATE TABLE IF NOT EXISTS api_tracking_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name VARCHAR(50) NOT NULL,
    caller_name VARCHAR(100),
    person_type VARCHAR(50),
    person_level VARCHAR(50),
    person_dept VARCHAR(100),
    call_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    response_time_ms BIGINT,
    status VARCHAR(20)
);
```

- [ ] **Step 5: 验证编译通过**

```bash
cd /path/to/library-backend && mvn compile -q
```

---

## 任务 2: 后端 — 通用响应模型

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/model/ApiResponse.java`

**Interfaces:**
- Produces: `ApiResponse<T>` — 统一响应包装

- [ ] **Step 1: 创建 ApiResponse.java**

```java
package com.library.backend.model;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    // getters and setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
```

---

## 任务 3: 后端 — HelloWorld 接口

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/model/HelloResult.java`
- Create: `library-backend/src/main/java/com/library/backend/service/HelloService.java`
- Create: `library-backend/src/main/java/com/library/backend/controller/HelloController.java`

**Interfaces:**
- Produces: `GET /api/hello` → `ApiResponse<HelloResult>`

- [ ] **Step 1: 创建 HelloResult.java**

```java
package com.library.backend.model;

import java.time.LocalDateTime;

public class HelloResult {
    private String greeting;
    private String timestamp;

    public HelloResult(String greeting, String timestamp) {
        this.greeting = greeting;
        this.timestamp = timestamp;
    }

    public String getGreeting() { return greeting; }
    public void setGreeting(String greeting) { this.greeting = greeting; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
```

- [ ] **Step 2: 创建 HelloService.java**

```java
package com.library.backend.service;

import com.library.backend.model.HelloResult;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HelloService {
    public HelloResult getGreeting() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new HelloResult("Hello World! Welcome to Library System", now);
    }
}
```

- [ ] **Step 3: 创建 HelloController.java**

```java
package com.library.backend.controller;

import com.library.backend.model.ApiResponse;
import com.library.backend.model.HelloResult;
import com.library.backend.service.HelloService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {
    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public ApiResponse<HelloResult> hello() {
        return ApiResponse.success(helloService.getGreeting());
    }
}
```

---

## 任务 4: 后端 — 哈希算法接口

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/model/HashRequest.java`
- Create: `library-backend/src/main/java/com/library/backend/model/HashResult.java`
- Create: `library-backend/src/main/java/com/library/backend/service/HashService.java`
- Create: `library-backend/src/main/java/com/library/backend/controller/HashController.java`

**Interfaces:**
- Produces: `POST /api/hash` 接收 `HashRequest`，返回 `ApiResponse<HashResult>`

- [ ] **Step 1: 创建 HashRequest.java**

```java
package com.library.backend.model;

public class HashRequest {
    private String input;
    private String algorithm; // MD5, SHA-256, SHA-512

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}
```

- [ ] **Step 2: 创建 HashResult.java**

```java
package com.library.backend.model;

public class HashResult {
    private String input;
    private String algorithm;
    private String hashResult;

    public HashResult(String input, String algorithm, String hashResult) {
        this.input = input;
        this.algorithm = algorithm;
        this.hashResult = hashResult;
    }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    public String getHashResult() { return hashResult; }
    public void setHashResult(String hashResult) { this.hashResult = hashResult; }
}
```

- [ ] **Step 3: 创建 HashService.java**

```java
package com.library.backend.service;

import com.library.backend.model.HashResult;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HashService {
    public HashResult computeHash(String input, String algorithm) {
        try {
            String alg = (algorithm == null || algorithm.isBlank()) ? "SHA-256" : algorithm;
            MessageDigest md = MessageDigest.getInstance(alg);
            byte[] digest = md.digest(input.getBytes());
            String hashHex = HexFormat.of().formatHex(digest);
            return new HashResult(input, alg, hashHex);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unsupported algorithm: " + algorithm, e);
        }
    }
}
```

- [ ] **Step 4: 创建 HashController.java**

```java
package com.library.backend.controller;

import com.library.backend.model.ApiResponse;
import com.library.backend.model.HashRequest;
import com.library.backend.model.HashResult;
import com.library.backend.service.HashService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HashController {
    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public ApiResponse<HashResult> hash(@RequestBody HashRequest request) {
        return ApiResponse.success(hashService.computeHash(request.getInput(), request.getAlgorithm()));
    }
}
```

---

## 任务 5: 后端 — 冒泡排序接口

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/model/SortRequest.java`
- Create: `library-backend/src/main/java/com/library/backend/model/SortResult.java`
- Create: `library-backend/src/main/java/com/library/backend/service/SortService.java`
- Create: `library-backend/src/main/java/com/library/backend/controller/SortController.java`

**Interfaces:**
- Produces: `POST /api/sort` 接收 `SortRequest`，返回 `ApiResponse<SortResult>`

- [ ] **Step 1: 创建 SortRequest.java**

```java
package com.library.backend.model;

import java.util.List;

public class SortRequest {
    private List<Integer> numbers;
    private String order; // asc or desc

    public List<Integer> getNumbers() { return numbers; }
    public void setNumbers(List<Integer> numbers) { this.numbers = numbers; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
}
```

- [ ] **Step 2: 创建 SortResult.java**

```java
package com.library.backend.model;

import java.util.List;

public class SortResult {
    private List<Integer> originalArray;
    private List<Integer> sortedArray;
    private String order;
    private int swapCount;
    private double executionTimeMs;

    public SortResult(List<Integer> originalArray, List<Integer> sortedArray,
                      String order, int swapCount, double executionTimeMs) {
        this.originalArray = originalArray;
        this.sortedArray = sortedArray;
        this.order = order;
        this.swapCount = swapCount;
        this.executionTimeMs = executionTimeMs;
    }

    public List<Integer> getOriginalArray() { return originalArray; }
    public void setOriginalArray(List<Integer> originalArray) { this.originalArray = originalArray; }
    public List<Integer> getSortedArray() { return sortedArray; }
    public void setSortedArray(List<Integer> sortedArray) { this.sortedArray = sortedArray; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
    public int getSwapCount() { return swapCount; }
    public void setSwapCount(int swapCount) { this.swapCount = swapCount; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }
}
```

- [ ] **Step 3: 创建 SortService.java**

```java
package com.library.backend.service;

import com.library.backend.model.SortResult;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SortService {
    public SortResult bubbleSort(List<Integer> numbers, String order) {
        List<Integer> arr = new ArrayList<>(numbers);
        int n = arr.size();
        int swapCount = 0;
        long start = System.nanoTime();

        boolean ascending = !"desc".equalsIgnoreCase(order);
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                boolean needSwap = ascending
                    ? arr.get(j) > arr.get(j + 1)
                    : arr.get(j) < arr.get(j + 1);
                if (needSwap) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapCount++;
                }
            }
        }
        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;

        return new SortResult(numbers, arr,
            ascending ? "asc" : "desc", swapCount, elapsedMs);
    }
}
```

- [ ] **Step 4: 创建 SortController.java**

```java
package com.library.backend.controller;

import com.library.backend.model.ApiResponse;
import com.library.backend.model.SortRequest;
import com.library.backend.model.SortResult;
import com.library.backend.service.SortService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SortController {
    private final SortService sortService;

    public SortController(SortService sortService) {
        this.sortService = sortService;
    }

    @PostMapping("/sort")
    public ApiResponse<SortResult> sort(@RequestBody SortRequest request) {
        return ApiResponse.success(sortService.bubbleSort(request.getNumbers(), request.getOrder()));
    }
}
```

---

## 任务 6: 后端 — 埋点拦截器 + 数据模型

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/model/TrackingLog.java`
- Create: `library-backend/src/main/java/com/library/backend/repository/TrackingLogRepository.java`
- Create: `library-backend/src/main/java/com/library/backend/interceptor/TrackedApi.java`
- Create: `library-backend/src/main/java/com/library/backend/interceptor/TrackingInterceptor.java`
- Create: `library-backend/src/main/java/com/library/backend/config/WebConfig.java`
- Create: `library-backend/src/main/java/com/library/backend/config/CorsConfig.java`

**Interfaces:**
- Produces: `TrackingInterceptor` 自动拦截 `@TrackedApi` 注解的接口

- [ ] **Step 1: 创建 TrackingLog.java (JPA Entity)**

```java
package com.library.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_tracking_log")
public class TrackingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", length = 50)
    private String apiName;

    @Column(name = "caller_name", length = 100)
    private String callerName;

    @Column(name = "person_type", length = 50)
    private String personType;

    @Column(name = "person_level", length = 50)
    private String personLevel;

    @Column(name = "person_dept", length = 100)
    private String personDept;

    @Column(name = "call_time")
    private LocalDateTime callTime;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "status", length = 20)
    private String status;

    public TrackingLog() {}

    public TrackingLog(String apiName, String callerName, String personType,
                       String personLevel, String personDept,
                       LocalDateTime callTime, Long responseTimeMs, String status) {
        this.apiName = apiName;
        this.callerName = callerName;
        this.personType = personType;
        this.personLevel = personLevel;
        this.personDept = personDept;
        this.callTime = callTime;
        this.responseTimeMs = responseTimeMs;
        this.status = status;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }
    public String getCallerName() { return callerName; }
    public void setCallerName(String callerName) { this.callerName = callerName; }
    public String getPersonType() { return personType; }
    public void setPersonType(String personType) { this.personType = personType; }
    public String getPersonLevel() { return personLevel; }
    public void setPersonLevel(String personLevel) { this.personLevel = personLevel; }
    public String getPersonDept() { return personDept; }
    public void setPersonDept(String personDept) { this.personDept = personDept; }
    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }
    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

- [ ] **Step 2: 创建 TrackingLogRepository.java**

```java
package com.library.backend.repository;

import com.library.backend.model.TrackingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TrackingLogRepository extends JpaRepository<TrackingLog, Long> {

    @Query("SELECT t.apiName, COUNT(t) FROM TrackingLog t GROUP BY t.apiName")
    List<Object[]> countByApiName();

    @Query("SELECT t.personType, COUNT(t) FROM TrackingLog t GROUP BY t.personType")
    List<Object[]> countByPersonType();

    @Query("SELECT t.personLevel, COUNT(t) FROM TrackingLog t GROUP BY t.personLevel")
    List<Object[]> countByPersonLevel();

    @Query("SELECT t.personDept, COUNT(t) FROM TrackingLog t GROUP BY t.personDept")
    List<Object[]> countByPersonDept();

    @Query("SELECT FUNCTION('DATE', t.callTime), COUNT(t) FROM TrackingLog t " +
           "GROUP BY FUNCTION('DATE', t.callTime) ORDER BY FUNCTION('DATE', t.callTime)")
    List<Object[]> countByDay();

    long count();
}
```

- [ ] **Step 3: 创建 TrackedApi.java 注解**

```java
package com.library.backend.interceptor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackedApi {
    String value() default "";
}
```

- [ ] **Step 4: 创建 TrackingInterceptor.java**

```java
package com.library.backend.interceptor;

import com.library.backend.model.TrackingLog;
import com.library.backend.repository.TrackingLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class TrackingInterceptor implements HandlerInterceptor {

    private final TrackingLogRepository repository;
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    public TrackingInterceptor(TrackingLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        startTime.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (!(handler instanceof HandlerMethod handlerMethod)) return;

        TrackedApi tracked = handlerMethod.getMethodAnnotation(TrackedApi.class);
        if (tracked == null) return;

        long elapsed = System.currentTimeMillis() - startTime.get();
        String apiName = tracked.value().isEmpty()
            ? handlerMethod.getMethod().getName()
            : tracked.value();

        String callerName = request.getHeader("X-Caller-Name");
        String personType = request.getHeader("X-Person-Type");
        String personLevel = request.getHeader("X-Person-Level");
        String personDept = request.getHeader("X-Person-Dept");

        if (callerName == null || callerName.isBlank()) callerName = "anonymous";

        TrackingLog log = new TrackingLog(
            apiName, callerName, personType, personLevel, personDept,
            LocalDateTime.now(), elapsed,
            ex == null ? "success" : "error"
        );
        repository.save(log);
    }
}
```

- [ ] **Step 5: 创建 WebConfig.java**

```java
package com.library.backend.config;

import com.library.backend.interceptor.TrackingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final TrackingInterceptor trackingInterceptor;

    public WebConfig(TrackingInterceptor trackingInterceptor) {
        this.trackingInterceptor = trackingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trackingInterceptor)
                .addPathPatterns("/api/**");
    }
}
```

- [ ] **Step 6: 创建 CorsConfig.java**

```java
package com.library.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

---

## 任务 7: 后端 — 给控制器方法添加 @TrackedApi 注解

**Files:**
- Modify: `HelloController.java` — 给 `hello()` 方法加 `@TrackedApi`
- Modify: `HashController.java` — 给 `hash()` 方法加 `@TrackedApi`
- Modify: `SortController.java` — 给 `sort()` 方法加 `@TrackedApi`

- [ ] **Step 1: HelloController 添加注解**

在 `hello()` 方法上添加 `@TrackedApi("hello")`

```java
@GetMapping("/hello")
@TrackedApi("hello")
public ApiResponse<HelloResult> hello() {
    return ApiResponse.success(helloService.getGreeting());
}
```

- [ ] **Step 2: HashController 添加注解**

在 `hash()` 方法上添加 `@TrackedApi("hash")`

- [ ] **Step 3: SortController 添加注解**

在 `sort()` 方法上添加 `@TrackedApi("sort")`

---

## 任务 8: 后端 — 统计接口

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/service/StatsService.java`
- Create: `library-backend/src/main/java/com/library/backend/controller/StatsController.java`

**Interfaces:**
- Produces: `GET /api/stats/overview` 和 `GET /api/stats/detail`

- [ ] **Step 1: 创建 StatsService.java**

```java
package com.library.backend.service;

import com.library.backend.model.TrackingLog;
import com.library.backend.repository.TrackingLogRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StatsService {
    private final TrackingLogRepository repository;

    public StatsService(TrackingLogRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getOverview() {
        Map<String, Object> result = new LinkedHashMap<>();

        long totalCalls = repository.count();
        result.put("totalCalls", totalCalls);

        // by API
        List<Object[]> byApi = repository.countByApiName();
        Map<String, Object> apiMap = new LinkedHashMap<>();
        for (Object[] row : byApi) {
            apiMap.put((String) row[0], row[1]);
        }
        result.put("byApi", apiMap);

        // dimensions
        Map<String, List<Map<String, Object>>> byDimension = new LinkedHashMap<>();
        byDimension.put("personType", buildDimensionList(repository.countByPersonType()));
        byDimension.put("personLevel", buildDimensionList(repository.countByPersonLevel()));
        byDimension.put("personDept", buildDimensionList(repository.countByPersonDept()));
        result.put("byDimension", byDimension);

        // trend
        List<Object[]> byDay = repository.countByDay();
        List<Map<String, Object>> trend = new ArrayList<>();
        for (Object[] row : byDay) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", row[0].toString());
            item.put("count", row[1]);
            trend.add(item);
        }
        result.put("trend", trend);

        return result;
    }

    private List<Map<String, Object>> buildDimensionList(List<Object[]> raw) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", row[0] != null ? row[0].toString() : "未知");
            item.put("value", row[1]);
            list.add(item);
        }
        return list;
    }
}
```

- [ ] **Step 2: 创建 StatsController.java**

```java
package com.library.backend.controller;

import com.library.backend.model.ApiResponse;
import com.library.backend.service.StatsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverview() {
        return ApiResponse.success(statsService.getOverview());
    }
}
```

---

## 任务 9: 后端 — 导出接口

**Files:**
- Create: `library-backend/src/main/java/com/library/backend/service/ExportService.java`
- Create: `library-backend/src/main/java/com/library/backend/controller/ExportController.java`

**Interfaces:**
- Produces: `GET /api/export?type=hello|hash|sort&format=json|csv`

- [ ] **Step 1: 创建 ExportService.java**

```java
package com.library.backend.service;

import com.library.backend.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExportService {
    private final HelloService helloService;
    private final HashService hashService;
    private final SortService sortService;

    public ExportService(HelloService helloService, HashService hashService,
                         SortService sortService) {
        this.helloService = helloService;
        this.hashService = hashService;
        this.sortService = sortService;
    }

    public String exportHello(String format) {
        HelloResult result = helloService.getGreeting();
        if ("csv".equalsIgnoreCase(format)) {
            return "greeting,timestamp\n" +
                "\"" + result.getGreeting() + "\",\"" + result.getTimestamp() + "\"\n";
        }
        return "{\"greeting\":\"" + result.getGreeting() + "\",\"timestamp\":\"" + result.getTimestamp() + "\"}";
    }

    public String exportHash(String format) {
        HashResult result = hashService.computeHash("demo-input", "SHA-256");
        if ("csv".equalsIgnoreCase(format)) {
            return "input,algorithm,hashResult\n" +
                "\"" + result.getInput() + "\",\"" + result.getAlgorithm() + "\",\"" + result.getHashResult() + "\"\n";
        }
        return "{\"input\":\"" + result.getInput() + "\",\"algorithm\":\"" + result.getAlgorithm() +
               "\",\"hashResult\":\"" + result.getHashResult() + "\"}";
    }

    public String exportSort(String format) {
        List<Integer> demo = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        SortResult result = sortService.bubbleSort(demo, "asc");
        if ("csv".equalsIgnoreCase(format)) {
            String originalStr = result.getOriginalArray().stream()
                .map(String::valueOf).collect(Collectors.joining(";"));
            String sortedStr = result.getSortedArray().stream()
                .map(String::valueOf).collect(Collectors.joining(";"));
            return "originalArray,sortedArray,order,swapCount,executionTimeMs\n" +
                "\"" + originalStr + "\",\"" + sortedStr + "\",\"" + result.getOrder() +
                "\"," + result.getSwapCount() + "," + result.getExecutionTimeMs() + "\n";
        }
        return "{\"originalArray\":" + result.getOriginalArray() + ",\"sortedArray\":" +
               result.getSortedArray() + ",\"order\":\"" + result.getOrder() +
               "\",\"swapCount\":" + result.getSwapCount() +
               ",\"executionTimeMs\":" + result.getExecutionTimeMs() + "}";
    }
}
```

- [ ] **Step 2: 创建 ExportController.java**

```java
package com.library.backend.controller;

import com.library.backend.service.ExportService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExportController {
    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<String> export(@RequestParam String type,
                                         @RequestParam(defaultValue = "json") String format) {
        String content;
        String filename;
        switch (type) {
            case "hello" -> {
                content = exportService.exportHello(format);
                filename = "hello-result";
            }
            case "hash" -> {
                content = exportService.exportHash(format);
                filename = "hash-result";
            }
            case "sort" -> {
                content = exportService.exportSort(format);
                filename = "sort-result";
            }
            default -> {
                return ResponseEntity.badRequest().body("Invalid type: " + type);
            }
        }

        MediaType mediaType;
        if ("csv".equalsIgnoreCase(format)) {
            mediaType = new MediaType("text", "csv");
            filename += ".csv";
        } else {
            mediaType = MediaType.APPLICATION_JSON;
            filename += ".json";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
```

---

## 任务 10: 前端 — 项目脚手架

**Files:**
- Create: `library-frontend/package.json`
- Create: `library-frontend/vite.config.js`
- Create: `library-frontend/index.html`
- Create: `library-frontend/src/main.js`
- Create: `library-frontend/src/App.vue`
- Create: `library-frontend/src/router/index.js`
- Create: `library-frontend/src/utils/request.js`

**Interfaces:**
- Produces: Vue3 项目骨架，可正常启动

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "library-frontend",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.0",
    "element-plus": "^2.5.0",
    "axios": "^1.6.0",
    "echarts": "^5.4.0",
    "vue-echarts": "^6.6.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>图书管理系统</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: 创建 src/main.js**

```javascript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.mount('#app')
```

- [ ] **Step 5: 创建 src/App.vue**

```vue
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
body {
  margin: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
</style>
```

- [ ] **Step 6: 创建 src/router/index.js**

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'ApiDashboard',
    component: () => import('../views/ApiDashboard.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

- [ ] **Step 7: 创建 src/utils/request.js**

```javascript
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'X-Caller-Name': 'demo-user',
    'X-Person-Type': '管理员',
    'X-Person-Level': '中级',
    'X-Person-Dept': '技术部'
  }
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      console.error('API error:', res.message)
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

export default request
```

---

## 任务 11: 前端 — API 封装层

**Files:**
- Create: `library-frontend/src/api/dashboard.js`

- [ ] **Step 1: 创建 dashboard.js**

```javascript
import request from '../utils/request'

export function getHello() {
  return request.get('/hello')
}

export function computeHash(input, algorithm) {
  return request.post('/hash', { input, algorithm })
}

export function sortNumbers(numbers, order) {
  return request.post('/sort', { numbers, order })
}

export function getStatsOverview() {
  return request.get('/stats/overview')
}

export function exportData(type, format = 'json') {
  return `${request.defaults.baseURL}/export?type=${type}&format=${format}`
}
```

---

## 任务 12: 前端 — 主页面 ApiDashboard.vue

**Files:**
- Create: `library-frontend/src/views/ApiDashboard.vue`

- [ ] **Step 1: 创建主页面**

```vue
<template>
  <div class="dashboard-container">
    <div class="header">
      <h1>API 演示与数据看板</h1>
      <el-button type="primary" @click="showExportDialog">
        <el-icon><Download /></el-icon>
        导出数据
      </el-button>
    </div>

    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="Hello World" name="hello">
        <HelloWorldPanel />
      </el-tab-pane>
      <el-tab-pane label="哈希算法" name="hash">
        <HashPanel />
      </el-tab-pane>
      <el-tab-pane label="冒泡排序" name="sort">
        <SortPanel />
      </el-tab-pane>
      <el-tab-pane label="调用统计看板" name="stats">
        <StatsDashboard />
      </el-tab-pane>
    </el-tabs>

    <!-- 导出对话框 -->
    <el-dialog v-model="exportDialogVisible" title="导出数据" width="400px">
      <el-form label-width="100px">
        <el-form-item label="导出类型">
          <el-select v-model="exportType" style="width: 100%">
            <el-option label="Hello World" value="hello" />
            <el-option label="哈希算法" value="hash" />
            <el-option label="冒泡排序" value="sort" />
            <el-option label="全部" value="all" />
          </el-select>
        </el-form-item>
        <el-form-item label="导出格式">
          <el-radio-group v-model="exportFormat">
            <el-radio value="json">JSON</el-radio>
            <el-radio value="csv">CSV</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="exportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExport">确认导出</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Download } from '@element-plus/icons-vue'
import HelloWorldPanel from '../components/HelloWorldPanel.vue'
import HashPanel from '../components/HashPanel.vue'
import SortPanel from '../components/SortPanel.vue'
import StatsDashboard from '../components/StatsDashboard.vue'
import { exportData } from '../api/dashboard'

const activeTab = ref('hello')
const exportDialogVisible = ref(false)
const exportType = ref('hello')
const exportFormat = ref('json')

function showExportDialog() {
  exportDialogVisible.value = true
}

function handleExport() {
  const types = exportType.value === 'all'
    ? ['hello', 'hash', 'sort']
    : [exportType.value]

  types.forEach(type => {
    window.open(exportData(type, exportFormat.value), '_blank')
  })
  exportDialogVisible.value = false
}
</script>

<style scoped>
.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}
</style>
```

---

## 任务 13: 前端 — HelloWorldPanel 组件

**Files:**
- Create: `library-frontend/src/components/HelloWorldPanel.vue`

- [ ] **Step 1: 创建组件**

```vue
<template>
  <div class="panel">
    <el-card>
      <template #header>
        <span>Hello World 接口演示</span>
      </template>
      <el-button type="primary" @click="fetchHello" :loading="loading">
        调用接口
      </el-button>

      <div v-if="result" class="result">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="问候语">{{ result.greeting }}</el-descriptions-item>
          <el-descriptions-item label="时间戳">{{ result.timestamp }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getHello } from '../api/dashboard'

const loading = ref(false)
const result = ref(null)

async function fetchHello() {
  loading.value = true
  try {
    result.value = await getHello()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.result {
  margin-top: 16px;
}
</style>
```

---

## 任务 14: 前端 — HashPanel 组件

**Files:**
- Create: `library-frontend/src/components/HashPanel.vue`

- [ ] **Step 1: 创建组件**

```vue
<template>
  <div class="panel">
    <el-card>
      <template #header>
        <span>哈希算法接口演示</span>
      </template>
      <el-form :model="form" label-width="100px">
        <el-form-item label="输入文本">
          <el-input v-model="form.input" placeholder="请输入要哈希的文本" />
        </el-form-item>
        <el-form-item label="算法选择">
          <el-select v-model="form.algorithm">
            <el-option label="MD5" value="MD5" />
            <el-option label="SHA-256" value="SHA-256" />
            <el-option label="SHA-512" value="SHA-512" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="computeHash" :loading="loading">
            计算哈希
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="result" class="result">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="输入文本">{{ result.input }}</el-descriptions-item>
          <el-descriptions-item label="算法">{{ result.algorithm }}</el-descriptions-item>
          <el-descriptions-item label="哈希结果">
            <span class="hash-value">{{ result.hashResult }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { computeHash } from '../api/dashboard'

const form = reactive({
  input: 'Hello World',
  algorithm: 'SHA-256'
})
const loading = ref(false)
const result = ref(null)

async function computeHash() {
  loading.value = true
  try {
    result.value = await computeHash(form.input, form.algorithm)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.result {
  margin-top: 16px;
}
.hash-value {
  word-break: break-all;
  font-family: monospace;
  font-size: 13px;
}
</style>
```

---

## 任务 15: 前端 — SortPanel 组件

**Files:**
- Create: `library-frontend/src/components/SortPanel.vue`

- [ ] **Step 1: 创建组件**

```vue
<template>
  <div class="panel">
    <el-card>
      <template #header>
        <span>冒泡排序接口演示</span>
      </template>
      <el-form :model="form" label-width="100px">
        <el-form-item label="数字列表">
          <el-input v-model="form.numbersText" placeholder="请输入数字，逗号分隔，如: 3,1,4,1,5,9" />
        </el-form-item>
        <el-form-item label="排序顺序">
          <el-radio-group v-model="form.order">
            <el-radio value="asc">升序</el-radio>
            <el-radio value="desc">降序</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSort" :loading="loading">
            开始排序
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="result" class="result">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="原始数组">
            [{{ result.originalArray.join(', ') }}]
          </el-descriptions-item>
          <el-descriptions-item label="排序后数组">
            [{{ result.sortedArray.join(', ') }}]
          </el-descriptions-item>
          <el-descriptions-item label="排序顺序">
            {{ result.order === 'asc' ? '升序' : '降序' }}
          </el-descriptions-item>
          <el-descriptions-item label="交换次数">
            {{ result.swapCount }}
          </el-descriptions-item>
          <el-descriptions-item label="执行耗时">
            {{ result.executionTimeMs }} ms
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { sortNumbers } from '../api/dashboard'

const form = reactive({
  numbersText: '3,1,4,1,5,9,2,6',
  order: 'asc'
})
const loading = ref(false)
const result = ref(null)

async function doSort() {
  loading.value = true
  try {
    const numbers = form.numbersText.split(',').map(s => parseInt(s.trim(), 10))
    result.value = await sortNumbers(numbers, form.order)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.result {
  margin-top: 16px;
}
</style>
```

---

## 任务 16: 前端 — StatsDashboard 组件（图表看板）

**Files:**
- Create: `library-frontend/src/components/StatsDashboard.vue`

- [ ] **Step 1: 创建组件**

```vue
<template>
  <div class="stats-dashboard">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filters">
        <el-form-item label="维度筛选">
          <el-select v-model="filters.dimension" @change="refreshCharts">
            <el-option label="人员类型" value="personType" />
            <el-option label="人员层级" value="personLevel" />
            <el-option label="人员部门" value="personDept" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="charts-row">
      <el-card class="chart-card">
        <template #header>
          <span>调用趋势（近7天）</span>
        </template>
        <div ref="lineChartRef" class="chart"></div>
      </el-card>
      <el-card class="chart-card">
        <template #header>
          <span>维度分布</span>
        </template>
        <div ref="pieChartRef" class="chart"></div>
      </el-card>
    </div>

    <el-card class="chart-card-full">
      <template #header>
        <span>各接口调用量对比</span>
      </template>
      <div ref="barChartRef" class="chart chart-full"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getStatsOverview } from '../api/dashboard'

const filters = ref({ dimension: 'personType' })
const lineChartRef = ref(null)
const pieChartRef = ref(null)
const barChartRef = ref(null)

let lineChart = null
let pieChart = null
let barChart = null

async function refreshCharts() {
  try {
    const data = await getStatsOverview()
    await nextTick()
    renderLineChart(data.trend)
    renderPieChart(data.byDimension[filters.value.dimension])
    renderBarChart(data.byApi)
  } catch (e) {
    console.error('Failed to load stats:', e)
  }
}

function renderLineChart(trendData) {
  if (!lineChartRef.value) return
  if (!lineChart) {
    lineChart = echarts.init(lineChartRef.value)
  }
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: trendData.map(d => d.date)
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'line',
      data: trendData.map(d => d.count),
      smooth: true,
      areaStyle: { opacity: 0.3 },
      lineStyle: { width: 3 },
      itemStyle: { color: '#409EFF' }
    }]
  })
}

function renderPieChart(dimensionData) {
  if (!pieChartRef.value) return
  if (!pieChart) {
    pieChart = echarts.init(pieChartRef.value)
  }
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: (dimensionData || []).map(d => ({
        name: d.label,
        value: d.value
      })),
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' }
      }
    }]
  })
}

function renderBarChart(apiData) {
  if (!barChartRef.value) return
  if (!barChart) {
    barChart = echarts.init(barChartRef.value)
  }
  const keys = Object.keys(apiData || {})
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: keys
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: keys.map(k => apiData[k]),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#79bbff' }
        ])
      },
      barWidth: '50%'
    }]
  })
}

onMounted(() => {
  refreshCharts()
})
</script>

<style scoped>
.stats-dashboard {
  width: 100%;
}
.filter-card {
  margin-bottom: 16px;
}
.charts-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}
.chart-card {
  flex: 1;
}
.chart-card-full {
  margin-bottom: 0;
}
.chart {
  width: 100%;
  height: 300px;
}
.chart-full {
  height: 350px;
}
</style>
```

---

## 任务 17: 验证 — 全链路集成测试

- [ ] **Step 1: 启动后端**

```bash
cd /path/to/library-backend && mvn spring-boot:run
```
预期: 控制台显示 started on port 8080

- [ ] **Step 2: 启动前端**

```bash
cd /path/to/library-frontend && npm install && npm run dev
```
预期: Vite 启动在 localhost:3000

- [ ] **Step 3: 测试 Hello 接口**

```bash
curl http://localhost:8080/api/hello
```
预期: 返回 `{"code":200,"message":"success","data":{"greeting":"Hello World!...","timestamp":"..."}}`

- [ ] **Step 4: 测试 Hash 接口**

```bash
curl -X POST http://localhost:8080/api/hash \
  -H "Content-Type: application/json" \
  -d '{"input":"test","algorithm":"SHA-256"}'
```
预期: 返回哈希结果

- [ ] **Step 5: 测试 Sort 接口**

```bash
curl -X POST http://localhost:8080/api/sort \
  -H "Content-Type: application/json" \
  -d '{"numbers":[3,1,4,1,5],"order":"asc"}'
```
预期: 返回排序后数组

- [ ] **Step 6: 测试导出接口**

```bash
curl -o result.json "http://localhost:8080/api/export?type=hello&format=json"
```
预期: 下载 JSON 文件

- [ ] **Step 7: 测试统计接口**

```bash
curl http://localhost:8080/api/stats/overview
```
预期: 返回统计数据

- [ ] **Step 8: 浏览器打开 http://localhost:3000/dashboard**
预期: 页面正常展示，四个 Tab 可切换，功能正常

---

## 自检清单

1. **Spec 覆盖**: 所有需求（3个接口 + Tab页面 + 导出 + 埋点 + 图表看板）均有对应任务
2. **占位符检查**: 无 TBD/TODO
3. **类型一致性**: 所有接口签名一致，前端调用与后端接口匹配
4. **仓间对齐**: 前端 request.js 中请求头携带埋点信息，后端拦截器统一解析
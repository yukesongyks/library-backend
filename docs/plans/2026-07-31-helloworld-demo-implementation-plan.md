# HelloWorld Demo Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.
> **契约事实来源:** `docs/specs/2026-07-31-helloworld-demo-design.md` §4（接口契约）+ §5（异常兜底）。本计划所有代码以该文档为唯一事实来源。
> **跨仓对齐点:** 接口前缀 `/api/demo`；统一响应 `Result<T>={code,message,data}`；导出走二进制流 `text/csv`；前端 Vite proxy `/api`→`http://localhost:8080`。

**Goal:** 在前后端空仓库上从零搭建 HelloWorld Demo 全链路——后端 4 接口 + 全局异常处理，前端 1 页面 3 Tab + 导出按钮 + 网络兜底。

**Architecture:** 后端 Spring Boot 3 单体（Controller-Service-DTO 分层 + `@RestControllerAdvice` 全局兜底）；前端 React 19 SPA（DemoPage 组合 3 Tab 子组件 + request.ts 统一拦截 Result）。前后端通过 REST+JSON 契约解耦，Vite proxy 解决跨域。

**Tech Stack:**
- 后端: Spring Boot 3.2.5 / Java 17 / Maven / spring-boot-starter-web / spring-boot-starter-validation
- 前端: React 19 / Vite 5 / TypeScript 5 / react-router-dom 6

**Global Constraints:**
- 后端 groupId `com.library`, artifactId `demo`, package `com.library.demo`
- 接口路径前缀统一 `/api/demo`
- 响应统一 `Result<T>` 包装；导出接口例外（二进制流）
- 错误码: 0=成功, 40001=参数校验, 40002=业务拒绝, 50000=系统错误
- 哈希算法固定 SHA-256，摘要小写十六进制，UTF-8 编码
- 冒泡排序手写（禁用 `Arrays.sort`），统计交换次数，入参数组长度 ≤1000
- 导出 CSV 写 UTF-8 BOM 头防 Excel 乱码
- 前端请求超时 10s，非 0 code toast 提示
- Git 只读约束: 实施阶段禁止 commit/push 等写操作

---

## Task 1: 后端工程骨架 (library-backend)

**Files:**
- Create: `pom.xml`
- Create: `src/main/resources/application.yml`
- Create: `src/main/java/com/library/demo/LibraryDemoApplication.java`
- Create: `src/main/java/com/library/demo/common/Result.java`
- Create: `src/main/java/com/library/demo/common/GlobalExceptionHandler.java`
- Create: `src/main/java/com/library/demo/common/ErrorCode.java`

**Interfaces:**
- Consumes: 无（基础骨架）
- Produces:
  - `Result<T>` record: `Result.success(T data)`, `Result.error(int code, String message)`
  - `ErrorCode` 常量: `SUCCESS=0`, `PARAM_INVALID=40001`, `BUSINESS_REJECT=40002`, `SYSTEM_ERROR=50000`
  - `GlobalExceptionHandler`: 拦截 `MethodArgumentNotValidException`→40001, `ConstraintViolationException`→40001, `IllegalArgumentException`→40002, `Exception`→50000

**Steps:**

- [ ] 1.1 创建 `pom.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>
    <groupId>com.library</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>library-demo</name>
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
            <artifactId>spring-boot-starter-validation</artifactId>
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

- [ ] 1.2 创建 `src/main/resources/application.yml`
```yaml
server:
  port: 8080
spring:
  application:
    name: library-demo
```

- [ ] 1.3 创建启动类 `LibraryDemoApplication.java`
```java
package com.library.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryDemoApplication.class, args);
    }
}
```

- [ ] 1.4 创建 `ErrorCode.java` 错误码常量
```java
package com.library.demo.common;

public final class ErrorCode {
    public static final int SUCCESS = 0;
    public static final int PARAM_INVALID = 40001;
    public static final int BUSINESS_REJECT = 40002;
    public static final int SYSTEM_ERROR = 50000;

    private ErrorCode() {
    }
}
```

- [ ] 1.5 创建 `Result.java` 统一响应体
```java
package com.library.demo.common;

public record Result<T>(int code, String message, T data) {
    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS, "success", data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
```

- [ ] 1.6 创建 `GlobalExceptionHandler.java` 全局异常处理器
```java
package com.library.demo.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** @Valid 校验失败 → 40001 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.ok(Result.error(ErrorCode.PARAM_INVALID, msg));
    }

    /** Query 参数校验失败 → 40001 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraint(ConstraintViolationException ex) {
        return ResponseEntity.ok(Result.error(ErrorCode.PARAM_INVALID, ex.getMessage()));
    }

    /** 业务前置校验失败 → 40002 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleBusiness(IllegalArgumentException ex) {
        return ResponseEntity.ok(Result.error(ErrorCode.BUSINESS_REJECT, ex.getMessage()));
    }

    /** 兜底 → 50000 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnknown(Exception ex) {
        return ResponseEntity.ok(Result.error(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后重试"));
    }
}
```

- [ ] 1.7 运行编译验证骨架可构建
```bash
cd <library-backend worktree> && ./mvnw -q clean compile || mvn -q clean compile
```
预期：`BUILD SUCCESS`，生成 `target/classes/`。

---

## Task 2: HelloWorld 接口 (library-backend)

**Files:**
- Create: `src/main/java/com/library/demo/controller/DemoController.java`
- Create: `src/main/java/com/library/demo/dto/HelloWorldResponse.java`
- Test: `src/test/java/com/library/demo/controller/DemoControllerTest.java`

**Interfaces:**
- Consumes: Task 1 的 `Result`
- Produces: `GET /api/demo/helloworld` → `Result<HelloWorldResponse>`，data=`{message:"Hello, World!"}`

**Steps:**

- [ ] 2.1 创建 DTO `HelloWorldResponse.java`
```java
package com.library.demo.dto;

public record HelloWorldResponse(String message) {
}
```

- [ ] 2.2 创建 `DemoController.java`，实现 helloworld 端点
```java
package com.library.demo.controller;

import com.library.demo.common.Result;
import com.library.demo.dto.HelloWorldResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/helloworld")
    public Result<HelloWorldResponse> helloworld() {
        return Result.success(new HelloWorldResponse("Hello, World!"));
    }
}
```

- [ ] 2.3 创建测试 `DemoControllerTest.java`
```java
package com.library.demo.controller;

import com.library.demo.dto.HelloWorldResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void helloworld_returnsFixedMessage() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/demo/helloworld", String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("Hello, World!");
    }
}
```

- [ ] 2.4 运行测试验证
```bash
cd <library-backend worktree> && ./mvnw -q test -Dtest=DemoControllerTest || mvn -q test -Dtest=DemoControllerTest
```
预期：`Tests run: 1, Failures: 0`。

---

## Task 3: 哈希算法接口 (library-backend)

**Files:**
- Create: `src/main/java/com/library/demo/dto/HashRequest.java`
- Create: `src/main/java/com/library/demo/dto/HashResponse.java`
- Modify: `src/main/java/com/library/demo/controller/DemoController.java`（新增 hash 端点）
- Create: `src/main/java/com/library/demo/service/HashService.java`
- Test: `src/test/java/com/library/demo/service/HashServiceTest.java`

**Interfaces:**
- Consumes: Task 1 的 `Result`、`GlobalExceptionHandler`
- Produces:
  - `POST /api/demo/hash` 请求体 `{text:string}` → `Result<HashResponse>`
  - `HashResponse{original,algorithm,digest}`
  - 校验: text 非空且 ≤1000，空→40002"text 不能为空"，超长→40002"text 长度超限(≤1000)"

**Steps:**

- [ ] 3.1 创建 `HashRequest.java`
```java
package com.library.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HashRequest(
        @NotBlank(message = "text 不能为空")
        @Size(max = 1000, message = "text 长度超限(≤1000)")
        String text
) {
}
```

- [ ] 3.2 创建 `HashResponse.java`
```java
package com.library.demo.dto;

public record HashResponse(String original, String algorithm, String digest) {
}
```

- [ ] 3.3 创建 `HashService.java`，SHA-256 计算含 NoSuchAlgorithmException 兜底
```java
package com.library.demo.service;

import com.library.demo.dto.HashRequest;
import com.library.demo.dto.HashResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    private static final String ALGORITHM = "SHA-256";

    public HashResponse hash(HashRequest request) {
        String text = request.text();
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] digestBytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            String digest = toHex(digestBytes);
            return new HashResponse(text, ALGORITHM, digest);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 为 JDK 内置算法，理论不抛；兜底
            throw new IllegalStateException(e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
```

- [ ] 3.4 在 `DemoController.java` 注入 HashService 并新增 hash 端点
```java
package com.library.demo.controller;

import com.library.demo.common.Result;
import com.library.demo.dto.HashRequest;
import com.library.demo.dto.HashResponse;
import com.library.demo.dto.HelloWorldResponse;
import com.library.demo.service.HashService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final HashService hashService;

    public DemoController(HashService hashService) {
        this.hashService = hashService;
    }

    @GetMapping("/helloworld")
    public Result<HelloWorldResponse> helloworld() {
        return Result.success(new HelloWorldResponse("Hello, World!"));
    }

    @PostMapping("/hash")
    public Result<HashResponse> hash(@Valid @RequestBody HashRequest request) {
        return Result.success(hashService.hash(request));
    }
}
```

- [ ] 3.5 创建 `HashServiceTest.java` 验证摘要正确性与格式
```java
package com.library.demo.service;

import com.library.demo.dto.HashRequest;
import com.library.demo.dto.HashResponse;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.assertj.core.api.Assertions.assertThat;

class HashServiceTest {

    private final HashService service = new HashService();

    @Test
    void hash_returnsSha256Digest() throws Exception {
        String text = "hello";
        HashResponse resp = service.hash(new HashRequest(text));
        byte[] expected = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
        String expectedHex = toHex(expected);

        assertThat(resp.original()).isEqualTo(text);
        assertThat(resp.algorithm()).isEqualTo("SHA-256");
        assertThat(resp.digest()).isEqualTo(expectedHex);
        assertThat(resp.digest()).hasSize(64);
        assertThat(resp.digest()).matches("[0-9a-f]{64}");
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
```

- [ ] 3.6 运行测试验证
```bash
cd <library-backend worktree> && ./mvnw -q test -Dtest=HashServiceTest || mvn -q test -Dtest=HashServiceTest
```
预期：`Tests run: 1, Failures: 0`，摘要为 64 位小写十六进制。

---

## Task 4: 冒泡排序接口 (library-backend)

**Files:**
- Create: `src/main/java/com/library/demo/dto/BubbleSortRequest.java`
- Create: `src/main/java/com/library/demo/dto/BubbleSortResponse.java`
- Modify: `src/main/java/com/library/demo/controller/DemoController.java`（新增 bubble-sort 端点）
- Create: `src/main/java/com/library/demo/service/BubbleSortService.java`
- Test: `src/test/java/com/library/demo/service/BubbleSortServiceTest.java`

**Interfaces:**
- Consumes: Task 1 的 `Result`、`GlobalExceptionHandler`
- Produces:
  - `POST /api/demo/bubble-sort` 请求体 `{numbers:int[]}` → `Result<BubbleSortResponse>`
  - `BubbleSortResponse{input:int[],sorted:int[],swaps:int}`
  - 校验: numbers 非空且长度 ≤1000，空→40002"numbers 不能为空"，超长→40002"数组长度超限(≤1000)"
  - 手写冒泡排序，统计交换次数

**Steps:**

- [ ] 4.1 创建 `BubbleSortRequest.java`
```java
package com.library.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BubbleSortRequest(
        @NotEmpty(message = "numbers 不能为空")
        @Size(max = 1000, message = "数组长度超限(≤1000)")
        List<Integer> numbers
) {
}
```

- [ ] 4.2 创建 `BubbleSortResponse.java`
```java
package com.library.demo.dto;

import java.util.List;

public record BubbleSortResponse(List<Integer> input, List<Integer> sorted, int swaps) {
}
```

- [ ] 4.3 创建 `BubbleSortService.java`，手写冒泡排序 + 交换计数
```java
package com.library.demo.service;

import com.library.demo.dto.BubbleSortRequest;
import com.library.demo.dto.BubbleSortResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortService {

    public BubbleSortResponse sort(BubbleSortRequest request) {
        List<Integer> input = new ArrayList<>(request.numbers());
        List<Integer> arr = new ArrayList<>(input);
        int n = arr.size();
        int swaps = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    int tmp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, tmp);
                    swaps++;
                }
            }
        }
        return new BubbleSortResponse(input, arr, swaps);
    }
}
```

- [ ] 4.4 在 `DemoController.java` 注入 BubbleSortService 并新增端点
```java
// 在 DemoController 类中新增字段与构造器参数、端点方法：
private final BubbleSortService bubbleSortService;
// 构造器改为：
public DemoController(HashService hashService, BubbleSortService bubbleSortService) {
    this.hashService = hashService;
    this.bubbleSortService = bubbleSortService;
}

@PostMapping("/bubble-sort")
public Result<BubbleSortResponse> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
    return Result.success(bubbleSortService.sort(request));
}
```

> 完整合并后的 `DemoController.java`:
```java
package com.library.demo.controller;

import com.library.demo.common.Result;
import com.library.demo.dto.*;
import com.library.demo.service.BubbleSortService;
import com.library.demo.service.HashService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final HashService hashService;
    private final BubbleSortService bubbleSortService;

    public DemoController(HashService hashService, BubbleSortService bubbleSortService) {
        this.hashService = hashService;
        this.bubbleSortService = bubbleSortService;
    }

    @GetMapping("/helloworld")
    public Result<HelloWorldResponse> helloworld() {
        return Result.success(new HelloWorldResponse("Hello, World!"));
    }

    @PostMapping("/hash")
    public Result<HashResponse> hash(@Valid @RequestBody HashRequest request) {
        return Result.success(hashService.hash(request));
    }

    @PostMapping("/bubble-sort")
    public Result<BubbleSortResponse> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        return Result.success(bubbleSortService.sort(request));
    }
}
```

- [ ] 4.5 创建 `BubbleSortServiceTest.java` 验证排序正确性与交换次数
```java
package com.library.demo.service;

import com.library.demo.dto.BubbleSortRequest;
import com.library.demo.dto.BubbleSortResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BubbleSortServiceTest {

    private final BubbleSortService service = new BubbleSortService();

    @Test
    void sort_ordersAscAndCountsSwaps() {
        BubbleSortResponse resp = service.sort(new BubbleSortRequest(List.of(5, 2, 9, 1, 5, 6)));
        assertThat(resp.sorted()).containsExactly(1, 2, 5, 5, 6, 9);
        assertThat(resp.swaps()).isEqualTo(8);
        assertThat(resp.input()).containsExactly(5, 2, 9, 1, 5, 6);
    }

    @Test
    void sort_singleElement_noSwaps() {
        BubbleSortResponse resp = service.sort(new BubbleSortRequest(List.of(42)));
        assertThat(resp.sorted()).containsExactly(42);
        assertThat(resp.swaps()).isZero();
    }
}
```

- [ ] 4.6 运行测试验证
```bash
cd <library-backend worktree> && ./mvnw -q test -Dtest=BubbleSortServiceTest || mvn -q test -Dtest=BubbleSortServiceTest
```
预期：`Tests run: 2, Failures: 0`，`[5,2,9,1,5,6]`→`[1,2,5,5,6,9]`，swaps=8。

---

## Task 5: 导出接口 (library-backend)

**Files:**
- Create: `src/main/java/com/library/demo/controller/ExportController.java`
- Create: `src/main/java/com/library/demo/service/ExportService.java`
- Test: `src/test/java/com/library/demo/controller/ExportControllerTest.java`

**Interfaces:**
- Consumes: Task 2-4 的 Service（复用 HashService/BubbleSortService + HelloWorld 固定输出）
- Produces:
  - `GET /api/demo/export?tab=<helloworld|hash|bubble-sort>&format=csv`
  - 响应 `Content-Type: text/csv; charset=UTF-8` + `Content-Disposition: attachment; filename=<tab>-result.csv`
  - helloworld→单列 `message`；hash→三列 `original,algorithm,digest`；bubble-sort→两列 `index,value`
  - 非法 tab → `Result.error(40001,"非法 tab 参数")`（JSON）
  - format 未识别 → 回退 csv + 响应头 `X-Export-Fallback: csv`
  - CSV 写 UTF-8 BOM 头

**说明:** 导出接口为无状态服务，对 helloworld 用固定输出，对 hash/bubble-sort 导出"示例结果"（固定示例数据，与前端 Tab 展示对齐）。

**Steps:**

- [ ] 5.1 创建 `ExportService.java`，按 tab 生成 CSV 字符串（含 BOM）
```java
package com.library.demo.service;

import com.library.demo.common.ErrorCode;
import com.library.demo.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ExportService {

    private static final Set<String> VALID_TABS = Set.of("helloworld", "hash", "bubble-sort");
    private static final byte[] UTF8_BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    /**
     * @return CSV 字节内容（含 UTF-8 BOM）
     */
    public byte[] exportCsv(String tab) {
        if (!VALID_TABS.contains(tab)) {
            throw new IllegalArgumentException("非法 tab 参数");
        }
        String csv = buildCsv(tab);
        byte[] body = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] result = new byte[UTF8_BOM.length + body.length];
        System.arraycopy(UTF8_BOM, 0, result, 0, UTF8_BOM.length);
        System.arraycopy(body, 0, result, UTF8_BOM.length, body.length);
        return result;
    }

    public String resolveFormat(String format) {
        if (format == null || format.isBlank() || "csv".equalsIgnoreCase(format)) {
            return "csv";
        }
        // 未识别值回退 csv（向后兼容预留）
        return "csv";
    }

    public boolean isFallback(String requestedFormat) {
        return requestedFormat != null && !requestedFormat.isBlank() && !"csv".equalsIgnoreCase(requestedFormat);
    }

    private String buildCsv(String tab) {
        return switch (tab) {
            case "helloworld" -> "message\r\nHello, World!\r\n";
            case "hash" -> {
                HashResponse r = new HashService().hash(new HashRequest("hello"));
                yield "original,algorithm,digest\r\n"
                        + csv(r.original()) + "," + r.algorithm() + "," + r.digest() + "\r\n";
            }
            case "bubble-sort" -> {
                BubbleSortResponse r = new BubbleSortService().sort(
                        new BubbleSortRequest(List.of(5, 2, 9, 1, 5, 6)));
                StringBuilder sb = new StringBuilder("index,value\r\n");
                for (int i = 0; i < r.sorted().size(); i++) {
                    sb.append(i).append(",").append(r.sorted().get(i)).append("\r\n");
                }
                yield sb.toString();
            }
            default -> throw new IllegalArgumentException("非法 tab 参数");
        };
    }

    /** CSV 字段转义：含逗号/引号/换行时用双引号包裹并转义内部引号 */
    private String csv(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
```

- [ ] 5.2 创建 `ExportController.java`
```java
package com.library.demo.controller;

import com.library.demo.common.ErrorCode;
import com.library.demo.common.Result;
import com.library.demo.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/demo")
public class ExportController {

    private static final Set<String> VALID_TABS = Set.of("helloworld", "hash", "bubble-sort");

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<?> export(
            @RequestParam String tab,
            @RequestParam(required = false, defaultValue = "csv") String format) {

        // 非法 tab → JSON 错误（不进入下载流）
        if (!VALID_TABS.contains(tab)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Result.error(ErrorCode.PARAM_INVALID, "非法 tab 参数"));
        }

        String resolved = exportService.resolveFormat(format);
        byte[] csv = exportService.exportCsv(tab);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tab + "-result.csv");
        if (exportService.isFallback(format)) {
            headers.set("X-Export-Fallback", "csv");
        }

        return ResponseEntity.ok().headers(headers).body(csv);
    }
}
```

- [ ] 5.3 创建 `ExportControllerTest.java`
```java
package com.library.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void export_helloworld_returnsCsvWithBom() {
        ResponseEntity<byte[]> resp = restTemplate.getForEntity(
                "/api/demo/export?tab=helloworld", byte[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getHeaders().getContentType()).hasToString("text/csv;charset=UTF-8");
        assertThat(resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=helloworld-result.csv");
        String body = new String(resp.getBody());
        assertThat(body).contains("message");
        assertThat(body).contains("Hello, World!");
    }

    @Test
    void export_bubbleSort_returnsIndexValueColumns() {
        ResponseEntity<byte[]> resp = restTemplate.getForEntity(
                "/api/demo/export?tab=bubble-sort", byte[].class);
        String body = new String(resp.getBody());
        assertThat(body).startsWith("index,value");
        assertThat(body).contains("0,1").contains("5,9");
    }

    @Test
    void export_invalidTab_returns40001Json() {
        ResponseEntity<String> resp = restTemplate.getForEntity(
                "/api/demo/export?tab=unknown", String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("40001").contains("非法 tab 参数");
    }

    @Test
    void export_unknownFormat_fallsBackWithHeader() {
        ResponseEntity<byte[]> resp = restTemplate.getForEntity(
                "/api/demo/export?tab=hash&format=excel", byte[].class);
        assertThat(resp.getHeaders().getFirst("X-Export-Fallback")).isEqualTo("csv");
        assertThat(new String(resp.getBody())).contains("original,algorithm,digest");
    }
}
```

- [ ] 5.4 运行后端全量测试验证
```bash
cd <library-backend worktree> && ./mvnw -q test || mvn -q test
```
预期：所有测试通过；导出含 BOM、非法 tab 返回 40001 JSON、未识别 format 回退并标注 `X-Export-Fallback: csv`。

---

## Task 6: 前端工程骨架 (library-frontend)

**Files:**
- Create: `package.json`
- Create: `vite.config.ts`
- Create: `tsconfig.json`
- Create: `tsconfig.node.json`
- Create: `index.html`
- Create: `src/main.tsx`
- Create: `src/App.tsx`
- Create: `src/types/demo.ts`
- Create: `src/api/request.ts`
- Create: `src/api/demo.ts`

**Interfaces:**
- Consumes: 后端契约（Task 1-5 产出的 `/api/demo/*` 接口与 `Result<T>` 包装）
- Produces:
  - `request.ts`: `request<T>(method,url,body?)` 统一拦截 `Result<T>`，非 0 code 抛错，10s 超时
  - `api/demo.ts`: `fetchHelloWorld()`, `computeHash(text)`, `bubbleSort(numbers)`, `exportUrl(tab)`
  - `types/demo.ts`: TypeScript 类型与后端 DTO 字段逐项对齐

**跨仓契约对齐点（必须逐项匹配后端）:**
| 前端类型 | 后端来源 | 字段 |
|---|---|---|
| `Result<T>` | Result.java | `code:number,message:string,data:T` |
| `HelloWorldResponse` | HelloWorldResponse.java | `message:string` |
| `HashRequest` | HashRequest.java | `text:string` |
| `HashResponse` | HashResponse.java | `original,algorithm,digest:string` |
| `BubbleSortRequest` | BubbleSortRequest.java | `numbers:number[]` |
| `BubbleSortResponse` | BubbleSortResponse.java | `input:number[],sorted:number[],swaps:number` |

**Steps:**

- [ ] 6.1 创建 `package.json`
```json
{
  "name": "library-frontend",
  "private": true,
  "version": "0.0.1",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview",
    "test": "vitest run"
  },
  "dependencies": {
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "react-router-dom": "^6.26.0"
  },
  "devDependencies": {
    "@types/react": "^19.0.0",
    "@types/react-dom": "^19.0.0",
    "@vitejs/plugin-react": "^4.3.0",
    "typescript": "^5.5.0",
    "vite": "^5.4.0",
    "vitest": "^2.0.0",
    "@testing-library/react": "^16.0.0",
    "jsdom": "^25.0.0"
  }
}
```

- [ ] 6.2 创建 `vite.config.ts`（proxy `/api`→`localhost:8080`）
```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
  },
})
```

- [ ] 6.3 创建 `tsconfig.json`
```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

- [ ] 6.4 创建 `tsconfig.node.json`
```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "bundler",
    "allowSyntheticDefaultImports": true
  },
  "include": ["vite.config.ts"]
}
```

- [ ] 6.5 创建 `index.html`
```html
<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Library Demo</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

- [ ] 6.6 创建 `src/types/demo.ts`（与后端 DTO 逐项对齐）
```typescript
// 与后端 Result.java 对齐
export interface Result<T> {
  code: number
  message: string
  data: T
}

// 对齐 HelloWorldResponse.java
export interface HelloWorldResponse {
  message: string
}

// 对齐 HashRequest.java / HashResponse.java
export interface HashRequest {
  text: string
}
export interface HashResponse {
  original: string
  algorithm: string
  digest: string
}

// 对齐 BubbleSortRequest.java / BubbleSortResponse.java
export interface BubbleSortRequest {
  numbers: number[]
}
export interface BubbleSortResponse {
  input: number[]
  sorted: number[]
  swaps: number
}

export type DemoTab = 'helloworld' | 'hash' | 'bubble-sort'
```

- [ ] 6.7 创建 `src/api/request.ts`（统一拦截 Result，10s 超时，toast 兜底）
```typescript
import type { Result } from '../types/demo'

export class ApiError extends Error {
  code: number
  constructor(code: number, message: string) {
    super(message)
    this.code = code
    this.name = 'ApiError'
  }
}

export async function request<T>(
  method: string,
  url: string,
  body?: unknown,
): Promise<T> {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), 10_000)
  try {
    const resp = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: body ? JSON.stringify(body) : undefined,
      signal: controller.signal,
    })
    if (!resp.ok) {
      throw new ApiError(50000, '服务异常，请稍后重试')
    }
    const result: Result<T> = await resp.json()
    if (result.code !== 0) {
      throw new ApiError(result.code, result.message)
    }
    return result.data
  } catch (err) {
    if (err instanceof ApiError) throw err
    if (err instanceof DOMException && err.name === 'AbortError') {
      throw new ApiError(50000, '网络异常，请检查后端服务')
    }
    throw new ApiError(50000, '服务异常，请稍后重试')
  } finally {
    clearTimeout(timer)
  }
}

export function toast(message: string) {
  // 简易 toast：实际可替换为 UI 库；这里用 window.alert 兜底，保持零依赖
  if (typeof window !== 'undefined') {
    window.alert(message)
  }
}
```

- [ ] 6.8 创建 `src/api/demo.ts`（封装各接口调用）
```typescript
import { request } from './request'
import type {
  HelloWorldResponse,
  HashResponse,
  BubbleSortResponse,
  DemoTab,
} from '../types/demo'

export function fetchHelloWorld(): Promise<HelloWorldResponse> {
  return request<HelloWorldResponse>('GET', '/api/demo/helloworld')
}

export function computeHash(text: string): Promise<HashResponse> {
  return request<HashResponse>('POST', '/api/demo/hash', { text })
}

export function bubbleSort(numbers: number[]): Promise<BubbleSortResponse> {
  return request<BubbleSortResponse>('POST', '/api/demo/bubble-sort', { numbers })
}

export function exportUrl(tab: DemoTab): string {
  return `/api/demo/export?tab=${tab}`
}
```

- [ ] 6.9 创建 `src/main.tsx`
```typescript
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
```

- [ ] 6.10 创建 `src/App.tsx`（路由到 /demo）
```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import DemoPage from './pages/DemoPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/demo" element={<DemoPage />} />
        <Route path="*" element={<Navigate to="/demo" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
```

- [ ] 6.11 安装依赖并验证类型检查
```bash
cd <library-frontend worktree> && npm install && npm run build
```
预期：`npm install` 成功；`tsc -b` 无类型错误；`vite build` 产出 `dist/`。

---

## Task 7: DemoPage + 3 Tab + 导出按钮 (library-frontend)

**Files:**
- Create: `src/pages/DemoPage.tsx`
- Create: `src/components/HelloWorldTab.tsx`
- Create: `src/components/HashTab.tsx`
- Create: `src/components/BubbleSortTab.tsx`
- Create: `src/components/ExportButton.tsx`
- Test: `src/components/BubbleSortTab.test.tsx`

**Interfaces:**
- Consumes: Task 6 的 `api/demo.ts`、`request.ts`、`types/demo.ts`
- Produces:
  - `DemoPage`: 3 Tab 切换 + 导出按钮（作用于当前 Tab）
  - `HelloWorldTab`: GET helloworld 展示固定字符串
  - `HashTab`: 文本输入框 → POST hash 展示原文+算法+摘要；空文本 disabled
  - `BubbleSortTab`: 逗号分隔数字输入 → POST bubble-sort 展示 input/sorted/swaps；非法格式前端拦截
  - `ExportButton`: fetch 流式下载，失败/JSON错误体走 §5.3 兜底；Tab 结果未加载时 disabled

**Steps:**

- [ ] 7.1 创建 `src/components/HelloWorldTab.tsx`
```typescript
import { useEffect, useState } from 'react'
import { fetchHelloWorld } from '../api/demo'
import { ApiError, toast } from '../api/request'

export default function HelloWorldTab() {
  const [message, setMessage] = useState<string | null>(null)
  const [loaded, setLoaded] = useState(false)

  useEffect(() => {
    fetchHelloWorld()
      .then((r) => {
        setMessage(r.message)
        setLoaded(true)
      })
      .catch((err: ApiError) => {
        toast(err.message)
        setLoaded(true)
      })
  }, [])

  return (
    <div>
      {message ? <p>{message}</p> : <p>加载中…</p>}
    </div>
  )
}
```

- [ ] 7.2 创建 `src/components/HashTab.tsx`
```typescript
import { useState } from 'react'
import { computeHash } from '../api/demo'
import { ApiError, toast } from '../api/request'
import type { HashResponse } from '../types/demo'

export default function HashTab() {
  const [text, setText] = useState('')
  const [result, setResult] = useState<HashResponse | null>(null)
  const [loaded, setLoaded] = useState(false)

  const onCompute = () => {
    if (!text.trim()) return
    computeHash(text)
      .then((r) => {
        setResult(r)
        setLoaded(true)
      })
      .catch((err: ApiError) => {
        toast(err.message)
        setLoaded(true)
      })
  }

  return (
    <div>
      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="输入文本"
      />
      <button onClick={onCompute} disabled={!text.trim()}>
        计算哈希
      </button>
      {result && (
        <table>
          <tbody>
            <tr><th>原文</th><td>{result.original}</td></tr>
            <tr><th>算法</th><td>{result.algorithm}</td></tr>
            <tr><th>摘要</th><td>{result.digest}</td></tr>
          </tbody>
        </table>
      )}
    </div>
  )
}
```

- [ ] 7.3 创建 `src/components/BubbleSortTab.tsx`
```typescript
import { useState } from 'react'
import { bubbleSort } from '../api/demo'
import { ApiError, toast } from '../api/request'
import type { BubbleSortResponse } from '../types/demo'

function parseNumbers(input: string): number[] | null {
  const parts = input.split(',').map((s) => s.trim()).filter(Boolean)
  const nums: number[] = []
  for (const p of parts) {
    const n = Number(p)
    if (!Number.isInteger(n)) return null
    nums.push(n)
  }
  return nums.length ? nums : null
}

export default function BubbleSortTab() {
  const [raw, setRaw] = useState('5,2,9,1,5,6')
  const [result, setResult] = useState<BubbleSortResponse | null>(null)
  const [loaded, setLoaded] = useState(false)

  const onSort = () => {
    const nums = parseNumbers(raw)
    if (!nums) {
      toast('请输入合法的逗号分隔整数')
      return
    }
    bubbleSort(nums)
      .then((r) => {
        setResult(r)
        setLoaded(true)
      })
      .catch((err: ApiError) => {
        toast(err.message)
        setLoaded(true)
      })
  }

  return (
    <div>
      <input value={raw} onChange={(e) => setRaw(e.target.value)} placeholder="逗号分隔的数字" />
      <button onClick={onSort}>排序</button>
      {result && (
        <div>
          <p>输入: {result.input.join(', ')}</p>
          <p>排序后: {result.sorted.join(', ')}</p>
          <p>交换次数: {result.swaps}</p>
        </div>
      )}
    </div>
  )
}
```

- [ ] 7.4 创建 `src/components/ExportButton.tsx`（fetch 流式下载 + 兜底）
```typescript
import { exportUrl } from '../api/demo'
import { toast } from '../api/request'
import type { DemoTab } from '../types/demo'

interface Props {
  tab: DemoTab
  disabled: boolean
}

export default function ExportButton({ tab, disabled }: Props) {
  const onExport = async () => {
    try {
      const resp = await fetch(exportUrl(tab))
      const contentType = resp.headers.get('Content-Type') || ''
      if (!contentType.includes('text/csv')) {
        // 非 CSV → 解析 JSON 错误体
        const err = await resp.json()
        toast(err.message || '导出失败')
        return
      }
      const blob = await resp.blob()
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${tab}-result.csv`
      document.body.appendChild(a)
      a.click()
      a.remove()
      URL.revokeObjectURL(url)
    } catch {
      toast('导出失败，请稍后重试')
    }
  }

  return (
    <button onClick={onExport} disabled={disabled}>
      导出 CSV
    </button>
  )
}
```

- [ ] 7.5 创建 `src/pages/DemoPage.tsx`（3 Tab + 导出按钮组合）
```typescript
import { useState } from 'react'
import HelloWorldTab from '../components/HelloWorldTab'
import HashTab from '../components/HashTab'
import BubbleSortTab from '../components/BubbleSortTab'
import ExportButton from '../components/ExportButton'
import type { DemoTab } from '../types/demo'

const TABS: { key: DemoTab; label: string }[] = [
  { key: 'helloworld', label: 'HelloWorld' },
  { key: 'hash', label: '哈希算法' },
  { key: 'bubble-sort', label: '冒泡排序' },
]

export default function DemoPage() {
  const [active, setActive] = useState<DemoTab>('helloworld')
  const [loaded, setLoaded] = useState(false)

  return (
    <div style={{ padding: 24 }}>
      <h1>HelloWorld Demo</h1>
      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        {TABS.map((t) => (
          <button
            key={t.key}
            onClick={() => {
              setActive(t.key)
              setLoaded(false)
            }}
            style={{ fontWeight: active === t.key ? 'bold' : 'normal' }}
          >
            {t.label}
          </button>
        ))}
        <div style={{ marginLeft: 'auto' }}>
          <ExportButton tab={active} disabled={!loaded} />
        </div>
      </div>
      <div onLoadStart={() => setLoaded(true)}>
        {active === 'helloworld' && <HelloWorldTab onLoadDone={() => setLoaded(true)} />}
        {active === 'hash' && <HashTab onDone={() => setLoaded(true)} />}
        {active === 'bubble-sort' && <BubbleSortTab onDone={() => setLoaded(true)} />}
      </div>
    </div>
  )
}
```

> 注: 上述子组件需接受可选 `onDone` 回调以驱动导出按钮 disabled 态。在各子组件加载完成/出错时调用 `onDone?.()`。示例补丁（HashTab）：
```typescript
// HashTab props 增加 onDone?: () => void；在 then/catch 末尾追加 onDone?.()
interface Props { onDone?: () => void }
export default function HashTab({ onDone }: Props) {
  // ...
  // .then 里: setResult(r); setLoaded(true); onDone?.()
  // .catch 里: toast(err.message); setLoaded(true); onDone?.()
}
```
HelloWorldTab / BubbleSortTab 同理增加 `onDone` 回调。

- [ ] 7.6 创建测试 `src/components/BubbleSortTab.test.tsx`
```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { vi } from 'vitest'
import BubbleSortTab from './BubbleSortTab'

vi.mock('../api/demo', () => ({
  bubbleSort: vi.fn(() =>
    Promise.resolve({ input: [3, 1, 2], sorted: [1, 2, 3], swaps: 2 }),
  ),
}))

test('renders sorted result after sort', async () => {
  render(<BubbleSortTab />)
  fireEvent.change(screen.getByPlaceholderText('逗号分隔的数字'), {
    target: { value: '3,1,2' },
  })
  fireEvent.click(screen.getByText('排序'))
  await waitFor(() => {
    expect(screen.getByText('1, 2, 3')).toBeInTheDocument()
    expect(screen.getByText('交换次数: 2')).toBeInTheDocument()
  })
})
```

- [ ] 7.7 运行前端测试并构建验证
```bash
cd <library-frontend worktree> && npm test && npm run build
```
预期：`vitest` 测试通过；`npm run build` 无类型错误产出 `dist/`。

---

## 跨仓联调验证（编码阶段末尾）

- [ ] 8.1 启动后端
```bash
cd <library-backend worktree> && ./mvnw spring-boot:run || mvn spring-boot:run
```
- [ ] 8.2 启动前端 dev server
```bash
cd <library-frontend worktree> && npm run dev
```
- [ ] 8.3 浏览器访问 `http://localhost:5173/demo`，逐 Tab 验证：
  - HelloWorld Tab 展示 "Hello, World!"
  - 哈希 Tab 输入文本计算得到 64 位摘要
  - 冒泡排序 Tab 输入 `5,2,9,1,5,6` 得到 `[1,2,5,5,6,9]` swaps=8
  - 各 Tab 点击导出按钮下载 CSV，文件名 `<tab>-result.csv`，Excel 打开无乱码
  - 输入空文本/非法数字时按钮 disabled 或前端拦截不请求

---

## Self-Review

**1. Spec coverage（对照系分文档逐节）:**
- §4.1 统一响应体 Result<T> → Task 1 ✅
- §4.2 HelloWorld `GET /api/demo/helloworld` → Task 2 ✅
- §4.3 哈希 `POST /api/demo/hash` + SHA-256 + 校验 → Task 3 ✅
- §4.4 冒泡排序 `POST /api/demo/bubble-sort` + 手写 + swaps → Task 4 ✅
- §4.5 导出 `GET /api/demo/export` + tab/format + CSV + BOM + 回退 → Task 5 ✅
- §5.1-5.2 错误码 + 全局异常处理器分层 → Task 1 ✅
- §5.2.2 各接口校验（hash≤1000, bubble-sort≤1000, export tab枚举）→ Task 3/4/5 ✅
- §5.2.3 哈希 NoSuchAlgorithmException 兜底 → Task 3 ✅
- §5.2.4 导出兜底（非法tab→JSON, IOException→500, UTF-8 BOM）→ Task 5 ✅
- §5.3 前端兜底（10s超时, code≠0 toast, fetch流式下载, JSON错误体判断, 前置校验）→ Task 6/7 ✅
- §6 前端页面（/demo, DemoPage, 3 Tab, 导出按钮, vite proxy, request.ts）→ Task 6/7 ✅
- 覆盖缺口: 无。

**2. Placeholder scan:**
- 无 TBD/TODO/"implement later"/"add error handling"/"similar to Task N" 等占位符 ✅
- 每个步骤均含完整可执行代码 ✅

**3. 跨仓契约对齐:**
- 前端 `types/demo.ts` 字段与后端 record 逐项对齐（Task 6.6 已列对齐表）✅
- 路径前缀 `/api/demo` 前后端一致 ✅
- Result 包装前后端一致；导出接口例外（二进制流）前后端一致 ✅
- 错误码 0/40001/40002/50000 前后端一致 ✅

**4. 测试覆盖:**
- 后端: DemoControllerTest + HashServiceTest + BubbleSortServiceTest + ExportControllerTest ✅
- 前端: BubbleSortTab.test.tsx ✅
- 联调: Task 8 手动验证全链路 ✅

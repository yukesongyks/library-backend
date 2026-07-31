# Design: HelloWorld 算法接口 + 导出 + 埋点分析全链路

## 架构概览

```
library-frontend (Vue 3)                    library-backend (Spring Boot)
┌───────────────────────────┐               ┌───────────────────────────────┐
│ AlgorithmPage (3 Tab)     │  HTTP /api/   │ AlgorithmController            │
│   HelloWorld/Hash/Bubble  │ ────────────▶ │   helloworld/hash/bubble-sort │
│ ExportButton × 3          │  HTTP /api/   │   export?type=                 │
│ AnalyticsDashboard        │               │                               │
│   ECharts (line/pie/bar)  │  HTTP /api/   │ CallLogAspect (AOP 自动埋点)    │
│   维度: 类型/层级/部门     │   analytics   │   ↓ 写入 CallLog → H2          │
└───────────────────────────┘               │ AnalyticsController            │
                                            │   /calls?dimension=&chartType= │
                                            └───────────────────────────────┘
```

## 数据模型

### User 实体（新建）
```java
@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(name = "personnel_type", length = 50)      // 人员类型：开发/测试/产品/运维
    private String personnelType;
    @Column(name = "personnel_level", length = 50)     // 人员层级：P5/P6/P7/M1/M2
    private String personnelLevel;
    @Column(length = 100)                              // 部门：技术部/产品部/运维部
    private String department;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
```

### CallLog 实体（新建）
```java
@Entity @Table(name = "call_logs")
public class CallLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)    private String apiType;      // helloworld/hash/bubble-sort
    @Column(name = "caller_user_id")          private Long callerUserId;
    @Column(name = "caller_username", length = 50) private String callerUsername;
    @Column(name = "personnel_type", length = 50)  private String personnelType;   // 冗余存储
    @Column(name = "personnel_level", length = 50) private String personnelLevel;  // 冗余存储
    @Column(length = 100)                      private String department;         // 冗余存储
    @Column(name = "called_at", nullable = false)  private LocalDateTime calledAt;
    @Column(name = "request_summary", length = 500) private String requestSummary;
    @Column(name = "response_status", length = 20)  private String responseStatus;  // SUCCESS/ERROR
    @Column(name = "response_data", length = 2000)  private String responseData;
    @PrePersist void onCreate() { calledAt = LocalDateTime.now(); }
}
```
> personnelType/Level/department 在 CallLog 冗余存储，避免分析报表频繁 JOIN；用户维度变更不影响历史记录。

## API 契约

| 方法 | 路径 | 请求体 | 响应体 |
|------|------|--------|--------|
| GET | `/api/algorithms/helloworld` | — | `{"result": "Hello, World!"}` |
| POST | `/api/algorithms/hash` | `{"text":"abc","algorithm":"SHA-256"}` | `{"result":"ba7816bf...","algorithm":"SHA-256"}` |
| POST | `/api/algorithms/bubble-sort` | `{"numbers":[5,3,8,1]}` | `{"result":[1,3,5,8],"input":[5,3,8,1]}` |
| GET | `/api/algorithms/export?type=helloworld` | — | `text/csv` 文件流 |
| GET | `/api/analytics/calls?dimension=&chartType=&startTime=&endTime=` | — | `{"chartType":"bar","dimension":"department","data":[{"label":"技术部","value":42}]}` |

**chartType 语义**：`line`=按天趋势(data 含 date+value) / `pie`=维度占比(data 含 label+value) / `bar`=维度调用量(data 含 label+value)

**调用人标识**：前端请求设 `X-User-Id` header → AOP 切面读取 → 查 User 获取维度 → 写入 CallLog

## 埋点切面

```java
@Aspect @Component
public class CallLogAspect {
    @AfterReturning(pointcut = "execution(* com.library.backend.controller.AlgorithmController.*(..))",
                    returning = "result")
    public void logCall(JoinPoint jp, Object result) {
        try {
            // 1. RequestContextHolder 获取请求 → 读 X-User-Id
            // 2. 查 User 获取 personnelType/Level/department
            // 3. 构建 CallLog 保存
        } catch (Exception e) {
            log.warn("埋点写入失败，不阻断主流程", e);  // 兜底：仅记日志
        }
    }
}
```

## 异常兜底设计

### 全局异常处理器
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)      // 参数校验/业务异常
    public ResponseEntity<Map<String,String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(NoSuchAlgorithmException.class)      // 哈希算法不支持
    public ResponseEntity<Map<String,String>> algoError(NoSuchAlgorithmException e) {
        return ResponseEntity.status(500).body(Map.of("error", "internal error"));
    }
    @ExceptionHandler(DataAccessException.class)            // 数据库异常
    public ResponseEntity<Map<String,String>> dbError(DataAccessException e) {
        return ResponseEntity.status(500).body(Map.of("error", "internal error"));
    }
}
```

### 兜底矩阵
| 层 | 异常 | 兜底 | 对外表现 |
|----|------|------|----------|
| Controller | 参数校验失败 | `@Valid` + `IllegalArgumentException` → 全局处理器 | HTTP 400 + `{"error":"..."}` |
| Service | 哈希算法不支持 | `NoSuchAlgorithmException` → 全局处理器 | HTTP 500 + `{"error":"internal error"}` |
| AOP 切面 | User 查询/CallLog 写入失败 | try-catch 包裹，记 WARN 日志，**不抛出** | 接口正常返回，埋点缺失（可接受降级） |
| Export | type 无效/空 | 参数校验 → `IllegalArgumentException` | HTTP 400 |
| Export | 无数据 | 正常返回 CSV（仅表头） | HTTP 200 + 空表体 |
| Analytics | 无数据 | 返回 `data: []` | HTTP 200 + 空数组 |
| Analytics | 维度/chartType 无效 | 参数校验 → `IllegalArgumentException` | HTTP 400 |
| DB | 不可用 | `DataAccessException` → 全局处理器 | HTTP 500 |

> **设计原则**：埋点切面异常**绝不阻断业务接口**——埋点是旁路逻辑，主流程算法结果必须正确返回。

## 前端组件树
```
AlgorithmPage (/algorithms)
  ├── TabContainer
  │   ├── HelloWorldTab   → GET helloworld  | ExportButton
  │   ├── HashTab         → POST hash        | ExportButton
  │   └── BubbleSortTab   → POST bubble-sort | ExportButton
  └── AnalyticsDashboard
      ├── DimensionSelector  (personnelType/personnelLevel/department)
      ├── ChartTypeSelector  (line/pie/bar)
      ├── DateRangePicker
      └── ChartContainer (ECharts)
```

## 前后端契约对齐
| 契约项 | 后端 | 前端 | 状态 |
|--------|------|------|------|
| HelloWorld 响应 | `{"result":"..."}` | 读 `response.result` | ✅ |
| Hash 请求/响应 | `{"text","algorithm"}` / `{"result","algorithm"}` | 同构 | ✅ |
| BubbleSort 请求/响应 | `{"numbers":[...]}` / `{"result","input"}` | 同构 | ✅ |
| Export | `text/csv` 流 | 触发浏览器下载 | ✅ |
| Analytics | `{"chartType","dimension","data"}` | 读 `data` 渲染 | ✅ |
| 调用人标识 | 读 `X-User-Id` | 设 `X-User-Id` | ✅ |

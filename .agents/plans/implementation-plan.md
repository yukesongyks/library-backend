# 实施计划 — hello world-1.0T2 (library-backend)

> 阶段：实施计划  
> 日期：2026-08-06  
> 关联需求：`.agents/changeset/dima.md`  
> 状态：📝 待执行

---

## 一、目标与范围

在 `library-backend` 仓库中从零搭建 Spring Boot 项目，交付以下后端能力：

1. 三个业务接口：`/api/hello`、`/api/hash`、`/api/bubble-sort`
2. 导出接口：`/api/export`（支持按 tab 类型导出 Excel）
3. 埋点记录：AOP 切面自动记录每次接口调用
4. 报表查询接口：`/api/stats`（支持多维度聚合查询）

**不包含**：前端代码、数据库生产部署、真实用户认证对接。

---

## 二、技术选型确认

| 项 | 选择 | 版本 |
|---|---|---|
| JDK | OpenJDK | 17 |
| Spring Boot | spring-boot-starter-web | 3.2.x |
| 数据库 | H2 (内存) | 2.2.x |
| ORM | Spring Data JPA | 随 Boot |
| Excel 导出 | EasyExcel | 3.3.x |
| API 文档 | springdoc-openapi | 2.3.x |
| 构建工具 | Maven | 3.9+ |

---

## 三、项目结构规划

```
library-backend/
├── pom.xml
├── src/main/java/com/library/backend/
│   ├── LibraryBackendApplication.java
│   ├── config/
│   │   └── CorsConfig.java
│   ├── controller/
│   │   ├── HelloController.java
│   │   ├── HashController.java
│   │   ├── BubbleSortController.java
│   │   ├── ExportController.java
│   │   └── StatsController.java
│   ├── service/
│   │   ├── HelloService.java
│   │   ├── HashService.java
│   │   ├── BubbleSortService.java
│   │   ├── ExportService.java
│   │   └── StatsService.java
│   ├── aspect/
│   │   └── ApiCallAspect.java
│   ├── entity/
│   │   └── ApiCallLog.java
│   ├── repository/
│   │   └── ApiCallLogRepository.java
│   ├── dto/
│   │   ├── ApiRequest.java
│   │   ├── ApiResponse.java
│   │   ├── ExportRequest.java
│   │   └── StatsQuery.java
│   └── enums/
│       └── ApiName.java
├── src/main/resources/
│   ├── application.yml
│   └── schema.sql
└── src/test/java/com/library/backend/
    └── LibraryBackendApplicationTests.java
```

---

## 四、API 契约设计

### 4.1 通用请求头/参数约定

所有业务接口统一接收以下参数（query 或 body 均可，本计划采用 JSON body）：

```json
{
  "userId": "string",
  "userType": "EMPLOYEE | CONTRACTOR | INTERN",
  "level": "string",
  "department": "string"
}
```

### 4.2 业务接口

| 方法 | 路径 | 请求体额外字段 | 响应 |
|------|------|---------------|------|
| POST | `/api/hello` | 无 | `{ "message": "Hello, World!" }` |
| POST | `/api/hash` | `"input": "string"` | `{ "input": "...", "hash": "sha256hex" }` |
| POST | `/api/bubble-sort` | `"array": [int]` | `{ "original": [...], "sorted": [...] }` |

### 4.3 导出接口

| 方法 | 路径 | 请求体 | 响应 |
|------|------|--------|------|
| POST | `/api/export` | `{ "tab": "hello\|hash\|bubble-sort", "filters": {...} }` | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` (二进制流) |

### 4.4 报表查询接口

| 方法 | 路径 | Query 参数 | 响应 |
|------|------|-----------|------|
| GET | `/api/stats` | `dimension=userType\|level\|department`, `startDate`, `endDate`, `apiName`(可选) | `{ "dimension": "...", "items": [{ "label": "...", "count": N }] }` |

---

## 五、数据模型

### 5.1 api_call_log 表

```sql
CREATE TABLE api_call_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(64)  NOT NULL,
    user_type   VARCHAR(32)  NOT NULL,
    level       VARCHAR(32),
    department  VARCHAR(64),
    api_name    VARCHAR(32)  NOT NULL,
    called_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_call_log_dimension ON api_call_log(user_type, level, department, api_name, called_at);
```

---

## 六、实施步骤

### Step 1: 项目脚手架初始化 [priority: high]
- 使用 Spring Initializr 或手动创建 `pom.xml`
- 引入依赖：spring-boot-starter-web, spring-boot-starter-data-jpa, h2, easyexcel, springdoc-openapi-starter-webmvc-ui, lombok
- 配置 `application.yml`：H2 内存库、JPA ddl-auto=create、端口 8080、CORS 允许前端 origin
- **验证**：`mvn clean compile` 通过

### Step 2: 实体与 Repository [priority: high]
- 创建 `ApiCallLog` 实体类（JPA Entity）
- 创建 `ApiCallLogRepository`（JpaRepository）
- 添加自定义查询方法：按维度 + 时间范围分组统计
- **验证**：单元测试验证 Repository 查询正确

### Step 3: 埋点切面 [priority: high]
- 创建 `ApiCallAspect`，使用 `@Around` 拦截 Controller 层
- 从请求体提取 userId/userType/level/department
- 异步写入 `api_call_log`（避免阻塞主流程）
- **验证**：调用任一接口后查表有记录

### Step 4: 三个业务接口 [priority: high]
- `HelloController` + `HelloService`：返回固定字符串
- `HashController` + `HashService`：SHA-256 哈希
- `BubbleSortController` + `BubbleSortService`：冒泡排序实现
- 统一响应包装 `ApiResponse<T>`
- **验证**：curl / Swagger UI 测试三个接口

### Step 5: 导出接口 [priority: medium]
- `ExportController` + `ExportService`
- 根据 tab 类型查询对应数据（可复用业务 Service 或查日志表）
- 使用 EasyExcel 动态生成 xlsx 写入 HttpServletResponse OutputStream
- 设置 Content-Type 和 Content-Disposition 头
- **验证**：Postman 下载文件可用 Excel 打开

### Step 6: 报表查询接口 [priority: medium]
- `StatsController` + `StatsService`
- 支持 dimension 参数切换分组维度
- 支持 startDate / endDate 时间范围过滤
- 支持 apiName 可选过滤
- 返回聚合结果列表
- **验证**：不同维度组合返回正确聚合数据

### Step 7: CORS 与全局异常处理 [priority: low]
- `CorsConfig` 允许前端开发端口（默认 5173）
- 全局 `@ControllerAdvice` 统一错误响应格式
- **验证**：前端跨域请求无 403

### Step 8: 集成测试与文档 [priority: low]
- 编写关键接口集成测试（@SpringBootTest）
- 启动后访问 `/swagger-ui.html` 验证 API 文档完整
- **验证**：`mvn test` 全部通过

---

## 七、仓间对齐检查清单

| 对齐项 | 后端责任 | 状态 |
|--------|---------|------|
| API 路径与请求/响应格式 | 按第四节契约实现 | ⬜ |
| 埋点字段一致性 | userId/userType/level/department/apiName/timestamp | ⬜ |
| 导出文件格式 | xlsx + 正确 MIME type | ⬜ |
| 报表数据格式 | `{ dimension, items: [{ label, count }] }` | ⬜ |
| CORS 配置 | 允许前端 origin | ⬜ |

---

## 八、风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| H2 内存库重启丢数据 | Demo 可接受，生产需迁移 | 表结构兼容 MySQL，后续仅改 datasource |
| 埋点切面性能 | 高并发下写入延迟 | 异步写入 + 批量 flush |
| EasyExcel 大文件 OOM | 导出数据量过大 | 流式写入，限制单次导出上限 10w 行 |
| 前后端契约不一致 | 联调失败 | 先出 Swagger，前端 mock 开发 |

---

## 九、验收标准

1. `mvn clean package` 构建成功
2. 启动后 Swagger UI 可见全部 5 个端点
3. 三个业务接口返回正确结果
4. 导出接口返回有效 xlsx 文件
5. 报表接口按三种维度返回正确聚合数据
6. 每次接口调用后 `api_call_log` 表新增记录
7. 前端跨域请求正常

# 编码实现变更摘要 — hello world-1.0T2 (library-backend)

> 阶段：编码实现  
> 日期：2026-08-06  
> 关联计划：`.agents/plans/implementation-plan.md`  
> 状态：✅ 已完成

---

## 一、变更文件清单

### 1.1 新增文件（17个）

#### DTO 层 (`src/main/java/com/library/backend/dto/`)
| 文件 | 说明 |
|------|------|
| `ApiRequest.java` | 通用请求体，含 userId/userType/level/department/input/array 字段 |
| `ApiResponse.java` | 统一响应包装，泛型结构 `{code, message, data}` |
| `ExportRequest.java` | 导出请求，含 tab + filters Map |
| `StatsQuery.java` | 报表查询参数，含 dimension/startDate/endDate/apiName |
| `StatsItem.java` | 报表聚合项，含 label + count |
| `StatsResponse.java` | 报表响应，含 dimension + items 列表 |

#### Service 层 (`src/main/java/com/library/backend/service/`)
| 文件 | 说明 |
|------|------|
| `HelloService.java` | 返回固定字符串 "Hello, World!" |
| `HashService.java` | SHA-256 哈希计算 |
| `BubbleSortService.java` | 冒泡排序实现（int[] 输入输出） |
| `ExportService.java` | EasyExcel 导出，按 tab 类型生成 xlsx |
| `StatsService.java` | 按维度(userType/level/department)聚合统计 |

#### Controller 层 (`src/main/java/com/library/backend/controller/`)
| 文件 | 路径 | 方法 | 说明 |
|------|------|------|------|
| `HelloController.java` | `/api/hello` | POST | Hello World 接口 |
| `HashController.java` | `/api/hash` | POST | 哈希算法接口 |
| `BubbleSortController.java` | `/api/bubble-sort` | POST | 冒泡排序接口 |
| `ExportController.java` | `/api/export` | POST | Excel 导出接口 |
| `StatsController.java` | `/api/stats` | GET | 报表查询接口 |

#### Aspect & Config
| 文件 | 说明 |
|------|------|
| `aspect/ApiCallAspect.java` | AOP 切面，拦截 Controller 层异步记录调用日志 |
| `config/CorsConfig.java` | CORS 配置，允许前端跨域访问 |
| `config/GlobalExceptionHandler.java` | 全局异常处理，统一错误响应格式 |

### 1.2 已有文件（未修改）
- `LibraryBackendApplication.java` — 主启动类，已含 `@EnableAsync`
- `entity/ApiCallLog.java` — JPA 实体
- `enums/ApiName.java` — API 名称枚举
- `repository/ApiCallLogRepository.java` — 数据访问层
- `application.yml` — 应用配置
- `schema.sql` — 数据库表结构
- `pom.xml` — Maven 依赖配置

---

## 二、API 契约实现对照

| 计划契约 | 实现状态 | 备注 |
|----------|----------|------|
| POST `/api/hello` → `{message}` | ✅ | HelloController + HelloService |
| POST `/api/hash` → `{input, hash}` | ✅ | HashController + HashService (SHA-256) |
| POST `/api/bubble-sort` → `{original, sorted}` | ✅ | BubbleSortController + BubbleSortService |
| POST `/api/export` → xlsx 二进制流 | ✅ | ExportController + ExportService (EasyExcel) |
| GET `/api/stats` → `{dimension, items[{label,count}]}` | ✅ | StatsController + StatsService |
| 埋点字段: userId/userType/level/department/apiName/timestamp | ✅ | ApiCallAspect 异步写入 |
| CORS 允许前端 origin | ✅ | CorsConfig 读取 yml 配置 |

---

## 三、关键设计决策

1. **DTO 设计**：`ApiRequest` 使用 `List<Integer>` 接收数组，Controller 层转换为 `int[]` 传给 Service
2. **导出请求**：`ExportRequest` 采用 `{tab, filters}` 结构，filters Map 支持灵活扩展过滤条件
3. **异步埋点**：使用 `@Async` + `CompletableFuture` 避免阻塞主业务流程
4. **统一响应**：所有业务接口通过 `ApiResponse.success()` 包装返回
5. **全局异常**：`GlobalExceptionHandler` 捕获异常并返回标准错误格式

---

## 四、仓间对齐点（待前端确认）

| 对齐项 | 后端实现 | 前端需适配 |
|--------|----------|------------|
| API Base URL | `http://localhost:8080/api` | 配置代理或 baseURL |
| 请求体格式 | JSON body 含用户信息字段 | 每次调用传递 userId/userType/level/department |
| 导出响应 | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` | 处理 blob 下载 |
| 报表维度 | `userType` / `level` / `department` | Tab 切换时传不同 dimension 参数 |
| 时间格式 | ISO 8601 (`yyyy-MM-dd`) | 日期选择器输出格式对齐 |

---

## 五、验证状态

| 验证项 | 状态 | 说明 |
|--------|------|------|
| Maven 编译 | ⚠️ 环境无 mvn | 代码静态检查通过，需本地验证 |
| Swagger UI | ⏳ 待启动验证 | 启动后访问 `/swagger-ui.html` |
| 单元测试 | ⏳ 待编写 | 计划 Step 8 内容 |
| 集成测试 | ⏳ 待执行 | 需完整运行环境 |

---

## 六、风险与注意事项

1. **H2 内存库**：重启后数据丢失，仅适用于 Demo 演示
2. **异步埋点**：高并发下需关注线程池配置，当前使用 Spring 默认
3. **EasyExcel 版本**：pom.xml 中指定 3.3.x，确保与 JDK 17 兼容
4. **CORS 配置**：生产环境需限制允许的 origin 列表

---

## 七、后续步骤建议

1. 本地安装 Maven 后执行 `mvn clean package` 验证构建
2. 启动应用并通过 Swagger UI 测试所有端点
3. 前端联调时确认请求/响应格式一致性
4. 补充单元测试覆盖核心 Service 逻辑
5. 考虑将 H2 替换为 MySQL/PostgreSQL 用于持久化存储

---

# 编码实现报告（补充）— 冒泡排序模块

> 阶段：编码实现（Coding Implementation）  
> 日期：2026-11-04  
> 关联系分：`.agents/system.changes/design.md` §4.3 冒泡排序接口  
> Skill：`dtazziboot-java-coding-standards`  
> 状态：✅ 已完成

## 模块进度追踪

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|:----:|------|:----:|:----:|:----:|:-----:|:----:|------|
| 1 | bubble-sort | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |

## READ — 上下文

- 模块职责：接收前端数组，执行升序冒泡排序并回传原始值、排序结果与时间戳。
- 关键类：`BubbleSortController`（接口层）、`BubbleSortService`（排序算法）、`ApiRequest`（请求 DTO，`array` 为 `List<Integer>`）、`ApiResponse`（统一响应）、`GlobalExceptionHandler`（`IllegalArgumentException` → 400）。
- 关键发现：`BubbleSortController` 中 `int[] array = request.getArray();` 与 `ApiRequest.array` 的 `List<Integer>` 类型不匹配，工程无法编译；`ApiRequest`/`ExportRequest` 使用 `javax.validation`，与 Spring Boot 3（`jakarta.validation`）不兼容且缺少 `spring-boot-starter-validation` 依赖。
- 已加载规范：`naming.md`、`comments.md`、`constants.md`、`unit-testing.md`、`datetime.md`。

## TEST — 单元测试

| 文件 | 方法数 | 覆盖场景 |
|------|:----:|----------|
| `src/test/java/com/library/backend/service/BubbleSortServiceTest.java` | 8 | 正常路径、负数/重复值、已升序、逆序、单元素、空数组、入参不可变、null 异常 |
| `src/test/java/com/library/backend/controller/BubbleSortControllerTest.java` | 4 | 正常响应字段、空数组、array 缺失 400、元素为 null 抛参异常 |

JUnit 5 + AssertJ，AAA 三段式；项目此前无测试目录，按镜像路径新建 `src/test/java`。

## IMPL — 实现

| 文件 | 变更 |
|------|------|
| `service/BubbleSortService.java` | 保留原地交换 + 无交换提前退出；null 入参抛 `IllegalArgumentException`；入参 `Arrays.copyOf` 保护；补类/方法 Javadoc |
| `controller/BubbleSortController.java` | `List<Integer>` → `int[]` 转换（`toIntArray`，元素 null 显式报错）；响应按系分补齐 `timestamp`（`Instant`，ISO-8601）；使用 `LinkedHashMap` 固定字段顺序；400 码抽为常量 |
| `dto/ApiRequest.java` | `javax.validation` → `jakarta.validation`（Boot 3 编译一致性） |
| `dto/ExportRequest.java` | 同上 |
| `pom.xml` | 新增 `spring-boot-starter-validation` 依赖 |

响应契约（与系分 §4.3 一致）：

```json
{"code":200,"message":"success","data":{"original":[5,3,8,1,2],"sorted":[1,2,3,5,8],"timestamp":"2026-11-04T01:47:57.940Z"}}
```

## CHECK — 规范检查

### L1 静态检查

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|:--------:|
| 命名规范 | 类名大驼峰、方法小驼峰、常量全大写 | ✅ |
| 注释规范 | 类/方法 Javadoc，方法内单行注释 | ✅ |
| 常量规范 | 消除魔法值（`BAD_REQUEST_CODE`） | ✅ |
| 异常日志 | `IllegalArgumentException` 由 `GlobalExceptionHandler` 统一处理并 `log.warn` | ✅ |
| 日期时间 | `Instant` 输出 ISO-8601，未使用 `java.sql.*` | ✅ |
| 单元测试 | 测试类与被测类同 package，AAA 结构，断言含描述 | ✅ |
| 安全规范 | 无 SQL 拼接；输入 null/空元素显式校验 | ✅ |

### L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 编译验证 | ✅ | 临时 Temurin JDK 17 + Maven 3.9.9 执行 `mvn test`：编译 23 个主源码，`BUILD SUCCESS` |
| 单测验证 | ✅ | `Tests run: 12, Failures: 0, Errors: 0, Skipped: 0` |
| 真实接口验证 | ✅ | `mvn spring-boot:run` 后 POST `/api/bubble-sort`：契约示例返回正确；6 组随机数组（含负数、重复、单元素）与 `sorted()` 对照全部一致；元素 null → 400；array 缺失 → 业务码 400；空数组 → 空结果 |

#### 📋 待人工验证

```bash
mvn compile -DskipTests
mvn test -Dtest=BubbleSortServiceTest,BubbleSortControllerTest
```

**发现问题**：修复 3 处既有编译缺陷（Controller 类型不匹配、`javax.validation` 包名、缺失 validation 依赖），无遗留问题。

## DOCS

- 编码报告：已写入 `.agents/system.changes/code.md`（本节）。
- 架构/模块文档：仓库无 `SSOT.md` 且未指定模块文档路径，按 skill 约定不新建硬编码 `docs/modules/` 目录。

## 变更文件清单

- 修改：`src/main/java/com/library/backend/service/BubbleSortService.java`
- 修改：`src/main/java/com/library/backend/controller/BubbleSortController.java`
- 修改：`src/main/java/com/library/backend/dto/ApiRequest.java`
- 修改：`src/main/java/com/library/backend/dto/ExportRequest.java`
- 修改：`pom.xml`
- 新增：`src/test/java/com/library/backend/service/BubbleSortServiceTest.java`
- 新增：`src/test/java/com/library/backend/controller/BubbleSortControllerTest.java`

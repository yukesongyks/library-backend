# Code Review Report

> **Change** `hello-world-1.0T2` · **分支** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4d166906-8c85-4bd4-` · **日期** `2026-08-06` · **审查者** AI

---

## 1. 审查范围

| # | 文件路径 | 归属 |
|---|---------|------|
| 1 | `aspect/ApiCallAspect.java` | 埋点切面 |
| 2 | `config/CorsConfig.java` | CORS 配置 |
| 3 | `config/GlobalExceptionHandler.java` | 全局异常处理 |
| 4 | `controller/BubbleSortController.java` | 冒泡排序接口 |
| 5 | `controller/ExportController.java` | 导出接口 |
| 6 | `controller/HashController.java` | 哈希接口 |
| 7 | `controller/HelloController.java` | Hello World 接口 |
| 8 | `controller/StatsController.java` | 统计报表接口 |
| 9 | `dto/ApiRequest.java` | 请求 DTO |
| 10 | `dto/ApiResponse.java` | 响应 DTO |
| 11 | `dto/ExportRequest.java` | 导出请求 DTO |
| 12 | `dto/StatsItem.java` | 统计项 DTO |
| 13 | `dto/StatsQuery.java` | 统计查询 DTO |
| 14 | `dto/StatsResponse.java` | 统计响应 DTO |
| 15 | `entity/ApiCallLog.java` | 埋点实体 |
| 16 | `enums/ApiName.java` | API 名称枚举 |
| 17 | `repository/ApiCallLogRepository.java` | 数据访问层 |
| 18 | `service/BubbleSortService.java` | 冒泡排序服务 |
| 19 | `service/ExportService.java` | 导出服务 |
| 20 | `service/HashService.java` | 哈希服务 |
| 21 | `service/HelloService.java` | Hello 服务 |
| 22 | `service/StatsService.java` | 统计服务 |
| 23 | `resources/schema.sql` | 数据库 Schema |

**共 23 个文件，全部为新增文件。**

---

## 2. 功能性检查（Step 2）

| REQ | 功能点 | 来源 | 关联文件 | 状态 |
|-----|--------|------|---------|------|
| REQ-1 | helloworld 接口 `/api/hello` | design.md §4 | HelloController, HelloService | ✅ |
| REQ-2 | 哈希算法接口 `/api/hash` | design.md §4 | HashController, HashService | ✅ |
| REQ-3 | 冒泡排序接口 `/api/bubble-sort` | design.md §4 | BubbleSortController, BubbleSortService | ✅ |
| REQ-4 | 导出接口 `/api/export/{type}` | design.md §4 | ExportController, ExportService | ✅ |
| REQ-5 | 统计报表接口 `/api/stats/query` | design.md §4 | StatsController, StatsService | ✅ |
| REQ-6 | AOP 埋点拦截记录调用日志 | design.md §3.2 | ApiCallAspect, ApiCallLog, ApiCallLogRepository | ✅ |
| REQ-7 | 统一响应封装 ApiResponse | design.md §3.2 | ApiResponse | ✅ |
| REQ-8 | CORS 跨域配置 | design.md §3.2 | CorsConfig | ✅ |
| REQ-9 | 全局异常处理 | design.md §3.2 | GlobalExceptionHandler | ✅ |
| REQ-10 | 多维度统计（userType/level/department） | design.md §4 | StatsService, StatsQuery | ✅ |
| REQ-11 | 数据库表结构 api_call_log | design.md §5 | schema.sql, ApiCallLog | ✅ |

**功能性结论：所有需求功能点均已实现，未发现功能性不符。**

---

## 3. 问题清单

### 3.1 P0 — 阻塞（必须修复）

| # | 规则 | 描述 | 文件:行号 | 建议 |
|---|------|------|----------|------|
| 1 | G16.2 | catch 块捕获异常后未记录日志，直接吞掉异常 | `ApiCallAspect.java:55` | 添加 `log.error("Failed to save API call log", e)` |
| 2 | G16.2 | catch 块捕获异常后未记录日志 | `ApiCallAspect.java:83` | 添加 `log.error(...)` 或重新抛出 |
| 3 | G16.2 | catch 块捕获异常后未记录日志 | `ExportService.java:66` | 添加 `log.error("Failed to parse date", e)` |
| 4 | G16.2 | catch 块捕获异常后未记录日志，直接抛 RuntimeException | `HashService.java:25` | 在 throw 前添加 `log.error(...)` |
| 5 | G16.2 | catch 块捕获异常后未记录日志 | `StatsService.java:62` | 添加 `log.error("Failed to parse date", e)` |

### 3.2 P1 — 推荐修复

| # | 规则 | 描述 | 文件:行号 | 建议 |
|---|------|------|----------|------|
| 6 | M016 | `LocalDateTime.now()` 使用默认时区，生产环境可能不一致 | `ApiCallLog.java:54` | 显式指定 `ZoneId.systemDefault()` 或使用 `Instant` |
| 7 | M016 | `LocalDateTime.now()` 使用默认时区 | `ExportService.java:62` | 同上 |
| 8 | M016 | `LocalDateTime.now()` 使用默认时区 | `StatsService.java:58` | 同上 |
| 9 | S10.2 | CORS 配置使用通配符 `*`，存在安全风险 | `CorsConfig.java:16` | 生产环境应限定具体域名 |

### 3.3 P2 — 参考改进

| # | 规则 | 描述 | 文件:行号 | 建议 |
|---|------|------|----------|------|
| 10 | A2.2 | 通配符导入 `import javax.persistence.*` | `ApiCallLog.java:4` | 改为显式导入 |
| 11 | A3.4 | 行宽超过 120 字符 | `ApiCallAspect.java:32` | 换行拆分 |
| 12 | A3.4 | 行宽超过 120 字符 | `ExportService.java:23` | 换行拆分 |
| 13 | A3.4 | 行宽超过 120 字符 | `ExportService.java:49` | 换行拆分 |
| 14 | A3.4 | 行宽超过 120 字符 | `StatsService.java:37` | 换行拆分 |
| 15 | A3.4 | 行宽超过 120 字符 | `StatsService.java:39` | 换行拆分 |

---

## 4. 可靠性检查摘要（Step 4）

| 类别 | 状态 | 说明 |
|------|------|------|
| G1 并发控制 | N/A | 当前为无状态 Service，无共享可变状态 |
| G2 资源释放 | ✅ | ExportService 中 OutputStream 通过 try-with-resources 管理 |
| G3 事务边界 | ⚠️ | 埋点写入未加事务注解，单条插入可接受但批量场景需评估 |
| G4 超时/重试 | N/A | 纯本地计算，无外部调用 |
| G5 边界条件 | ✅ | BubbleSortService 对空数组/null 有防御 |
| S1 SQL 注入 | ✅ | 使用 JPA Repository 参数化查询 |
| S2 输入校验 | ⚠️ | Controller 层缺少 `@Valid` / `@NotBlank` 等校验注解 |
| S10 CORS | ❌ | 通配符配置（见 P1-9） |
| B/M/I Bug 模式 | ❌ | 5 处 catch 吞异常（见 P0） |

---

## 5. 可读性检查摘要（Step 3）

| 规则 | 状态 | 说明 |
|------|------|------|
| A1 源文件格式 | ✅ | UTF-8，LF 换行 |
| A2 导入规范 | ⚠️ | 1 处通配符导入（P2-10） |
| A3 行宽/缩进 | ⚠️ | 5 处行宽超限（P2-11~15） |
| A4 命名规范 | ✅ | 类名/方法名/变量名符合驼峰规范 |
| A5 注释规范 | ✅ | 关键 DTO 字段有 Javadoc |
| A6 方法长度 | ✅ | 所有方法均在 50 行以内 |
| A7 类职责单一 | ✅ | Controller/Service/Repository 分层清晰 |

---

## 6. 自定义扩展检查（Step 5）

N/A（未启用自定义规则）

---

## 7. 跨仓对齐点检查

| 对齐点 | 后端实现 | 契约一致性 |
|--------|---------|-----------|
| `/api/hello` POST → `{message}` | ✅ HelloController | 与设计文档一致 |
| `/api/hash` POST → `{input, hash}` | ✅ HashController | 与设计文档一致 |
| `/api/bubble-sort` POST → `{original, sorted}` | ✅ BubbleSortController | 与设计文档一致 |
| `/api/export/{type}` POST → Excel/CSV | ✅ ExportController | 与设计文档一致 |
| `/api/stats/query` POST → `{dimension, items[]}` | ✅ StatsController | 与设计文档一致 |
| 埋点字段 userId/userType/level/department/apiName/calledAt | ✅ ApiCallLog | 与 schema.sql 一致 |

---

## 8. 总结

| 等级 | 数量 |
|------|------|
| **P0 (Blocker)** | **5** |
| P1 (Major) | 4 |
| P2 (Info) | 6 |
| **合计** | **15** |

**审查结论：❌ 不通过**

存在 5 个 P0 级阻塞问题（catch 块吞异常未记录日志），必须修复后方可合并。P1 级问题建议在合并前修复。P2 级问题可作为后续优化项。

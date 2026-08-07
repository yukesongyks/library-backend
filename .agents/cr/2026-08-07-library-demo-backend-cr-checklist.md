# Code Review Checklist

> **Change** `library-demo-backend` · **分支/Commit** `AI/task-DEV-966dcd0a` / `c939d3b` · **日期** `2026-08-07`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已在被审仓库对变更路径运行 `scan-all-rules.sh`，输出已贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `src/main/java/com/antdigital/library/LibraryBackendApplication.java` | 启动类 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | `src/main/java/com/antdigital/library/common/exception/ErrorCodeEnum.java` | REQ-异常体系 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 3 | `src/main/java/com/antdigital/library/common/exception/GlobalExceptionHandler.java` | REQ-异常体系 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 4 | `src/main/java/com/antdigital/library/common/exception/ServiceException.java` | REQ-异常体系 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 5 | `src/main/java/com/antdigital/library/common/response/ApiResponse.java` | REQ-统一响应 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 6 | `src/main/java/com/antdigital/library/demo/controller/DemoController.java` | REQ-1/2/3 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 7 | `src/main/java/com/antdigital/library/demo/controller/ExportController.java` | REQ-4导出 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 8 | `src/main/java/com/antdigital/library/demo/model/vo/BubbleSortVO.java` | REQ-3 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 9 | `src/main/java/com/antdigital/library/demo/model/vo/HashVO.java` | REQ-2 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 10 | `src/main/java/com/antdigital/library/demo/model/vo/HelloWorldVO.java` | REQ-1 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 11 | `src/main/java/com/antdigital/library/demo/service/DemoService.java` | REQ-1/2/3 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 12 | `src/main/java/com/antdigital/library/demo/service/ExportService.java` | REQ-4 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 13 | `src/main/java/com/antdigital/library/demo/service/impl/DemoServiceImpl.java` | REQ-1/2/3 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |
| 14 | `src/main/java/com/antdigital/library/demo/service/impl/ExportServiceImpl.java` | REQ-4 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |
| 15 | `src/main/java/com/antdigital/library/track/aspect/TrackAspect.java` | REQ-5埋点 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |
| 16 | `src/main/java/com/antdigital/library/track/controller/TrackController.java` | REQ-5统计 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 17 | `src/main/java/com/antdigital/library/track/model/entity/TrackRecordDO.java` | REQ-5 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 18 | `src/main/java/com/antdigital/library/track/model/vo/TrackStatisticsVO.java` | REQ-5 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 19 | `src/main/java/com/antdigital/library/track/repository/TrackRecordRepository.java` | REQ-5 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ |
| 20 | `src/main/java/com/antdigital/library/track/service/TrackService.java` | REQ-5 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 21 | `src/main/java/com/antdigital/library/track/service/impl/TrackServiceImpl.java` | REQ-5 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |

> **注**：`WebMvcConfig.java` 在 inputs_content 中列出但实际不存在于代码库（`common/` 下仅有 `exception/` 和 `response/`），审查中跳过。

---

## Step 2 — 功能（产物 B）

> REQ 来源：`<requirement_section>` 原文

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 后端启动 When 调用 GET /api/demo/helloworld Then 返回欢迎消息 | "分别写三个接口helloworld" | `DemoController.java:39-43` `DemoServiceImpl.java:36-41` | ✅ | 返回 `HelloWorldVO{message, timestamp}` |
| REQ-2 | Given 输入字符串 When 调用 GET /api/demo/hash Then 返回SHA-256哈希 | "哈希算法" | `DemoController.java:51-55` `DemoServiceImpl.java:43-59` | ✅ | 使用 `MessageDigest.getInstance("SHA-256")`，返回 `HashVO` |
| REQ-3 | Given 逗号分隔数字 When 调用 GET /api/demo/bubble-sort Then 返回排序结果 | "冒泡排序" | `DemoController.java:63-67` `DemoServiceImpl.java:61-111` | ✅ | `bubbleSortInternal()` 升序排序，返回 `BubbleSortVO` |
| REQ-4 | Given 页面展示结果 When 调用 GET /api/demo/export?type=xxx Then 下载CSV | "后台提供导出接口，支持导出各个页面的展示结果" | `ExportController.java:38-45` `ExportServiceImpl.java:40-62` | ✅ | 支持三种类型 CSV 导出，含 UTF-8 BOM |
| REQ-5 | Given 接口被调用 When AOP切面拦截 Then 记录调用人和调用次数 | "后端再做个埋点，获取调用次数和调用人" | `TrackAspect.java:41-80` `TrackServiceImpl.java:45-60` | ✅ | `@Around` 拦截 demo.controller，从请求头获取用户信息 |
| REQ-6 | Given 维度参数 When 调用 GET /api/track/statistics Then 返回统计结果 | "根据不同的维度：人员类型、人员层级、人员部门等" | `TrackController.java:37-43` `TrackServiceImpl.java:62-110` | ✅ | 支持 user_type/user_level/user_department/user_id 四维度 |
| REQ-7 | Given 图表类型 When 查询统计 Then 返回折线/饼/柱状图数据 | "折线图以及饼图和柱状图不同展示形式" | `TrackServiceImpl.java:83-106` `TrackStatisticsVO.java` | ⚠️ | line 类型返回 timeSeriesData，pie/bar 返回 categoryData；但 `TrackRecordRepository` 编译错误导致功能不可用 |

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 所有文件 package 声明、编码一致 |
| A2 | 源文件结构/import 顺序 | ✅ | import 分组合理（java → javax → org → com） |
| A3 | 代码样式 | ⚠️ | `TrackRecordRepository.java:37` 行宽超限（A3.4，预扫命中） |
| A4 | 命名规范 | ✅ | 类名/方法名/变量名遵循驼峰，常量全大写下划线 |
| A5 | 编码实践 | ✅ | 无魔法值滥用（HASH_ALGORITHM 等已提常量） |
| A6 | 特定元素样式 | ✅ | 无违反 |
| A7 | Javadoc 规范 | ✅ | 所有 public 方法均有 Javadoc，含 @param/@return |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫结果（`scan-all-rules.sh`）：
> ```
> [P0] G16.2 — CatchWithoutLogging: DemoServiceImpl.java:55 (误报，见下)
> [P0] G16.2 — CatchWithoutLogging: DemoServiceImpl.java:75 (误报)
> [P0] G16.2 — CatchWithoutLogging: ExportServiceImpl.java:58 (误报)
> [P0] G16.2 — CatchWithoutLogging: TrackAspect.java:63 (误报)
> [P0] G16.2 — CatchWithoutLogging: TrackAspect.java:73 (误报)
> [P1] M016 — JavaTimeDefaultTimeZone: DemoServiceImpl.java:39
> [P1] M016 — JavaTimeDefaultTimeZone: TrackServiceImpl.java:56
> [P2] A3.4 — LineWidthExceeded: TrackRecordRepository.java:37
> Summary: 8 findings (P0=5, P1=2, P2=1) | 52/222 rules scanned
> ```

| ID | 状态 | 备注 |
|----|------|------|
| M016 | ❌ | `DemoServiceImpl.java:39` `LocalDateTime.now()` 未指定时区；`TrackServiceImpl.java:56` 同 |
| G16.2 | ⚠️ | 预扫报 5 处 CatchWithoutLogging，**全部为误报**：DemoServiceImpl:55 有 `logger.error`(L56)；DemoServiceImpl:75 有 `logger.warn`(L76)；ExportServiceImpl:58 有 `logger.error`(L59)；TrackAspect:63 有 `logger.warn`(L64)；TrackAspect:73 有 `logger.error`(L75) |
| B001–B081 | N/A | 预扫覆盖 25/81 条，无命中；其余需 AST/类型信息，LLM 逐文件审查未发现 Blocker 级 Bug 模式 |
| M001–M015 | N/A | 预扫覆盖 6/27 条，无命中；LLM 审查未发现 Major 级问题 |
| M017–M027 | N/A | 同上 |
| I001–I010 | N/A | 预扫覆盖 2/10 条，无命中 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A(无并发场景) | 本变更无多线程共享状态 |
| G2.1–G2.3 | N/A(无外部调用) | 无 RPC/HTTP 客户端调用 |
| G3.1–G3.2 | N/A(无事务嵌套) | 仅 `saveTrackRecord` 有 `@Transactional` |
| G4.1–G4.4 | N/A(无资源池) | OutputStream 已 try-with-resources |
| G5.1 | ✅ | 无缓存场景 |
| G6.1–G6.2 | N/A(无MQ) | 无消息队列 |
| G7.1–G7.2 | N/A(无幂等需求) | 埋点记录允许重复 |
| G8.1–G8.7 | N/A(无超时配置) | Demo 功能无外部依赖 |
| G9.1–G9.3 | N/A(无灰度) | 不涉及 |
| G10.1–G10.3 | N/A(无监控埋点框架) | 自建埋点已覆盖 |
| G11.1–G11.4 | N/A(无DB迁移) | ddl-auto=update |
| G12.1–G12.2 | N/A(无限流) | Demo 场景 |
| G13.1 | N/A(无日志框架配置) | 使用 SLF4J 标准 |
| G14.1–G14.4 | N/A(无DB连接池调优) | H2 内存数据库 |
| G15.1–G15.3 | N/A(无降级) | 不涉及 |
| G16.1 | ✅ | 所有 catch 块均有处理（throw 或业务逻辑） |
| G16.2 | ⚠️(误报) | 预扫 5 处全部误报，所有 catch 块均有日志记录 |
| G16.3 | ✅ | 无空 catch 块 |
| G16.4 | ✅ | 无 catch(Exception) 后无处理 |
| G17.1–G17.3 | N/A(无应急) | 不涉及 |
| G18.1–G18.3 | N/A(安全补强) | 不涉及 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ❌ | `TrackRecordRepository.java:25-27,37-39` — `@Query` 中通过 `+ :dimension +` 拼接列名，存在 SQL 注入风险。虽然 `TrackServiceImpl` 有白名单（`ALLOWED_DIMENSIONS`），但 Repository 层无独立防御，违反纵深防御原则。更严重的是：**该写法是 Java 编译错误**（`:dimension` 非合法 Java 表达式，详见 report P0-1） |
| S1.2 | ✅ | JPA 参数绑定用于值参数（非列名场景） |
| S1.3 | N/A | 无 MyBatis `${}` |
| S2.1–S2.3 | N/A(无认证) | Demo 场景无认证 |
| S3.1–S3.3 | N/A(无授权) | 不涉及 |
| S4.1–S4.2 | N/A(无敏感数据) | 不涉及 |
| S5.1–S5.2 | N/A(无密钥) | 不涉及 |
| S6.1–S6.3 | N/A(无文件上传) | 不涉及 |
| S7.1–S7.3 | N/A(无反序列化) | 不涉及 |
| S8.1–S8.4 | N/A(无SSRF) | 不涉及 |
| S9.1–S9.4 | N/A(无XSS) | 纯 API 返回 JSON |
| S10.1 | N/A(无CSRF) | 无状态 API |
| S10.2 | ⚠️ | 无 CORS 配置（inputs_content 列出的 `WebMvcConfig.java` 实际不存在），前端跨域调用可能失败 |
| S10.3 | N/A(无跳转) | 不涉及 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1–U2.3 | N/A(未启用自定义规则) | `customized-checklist.md` 为空/示例项 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（`N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`

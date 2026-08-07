# Code Review Report

> **Change** `library-demo-backend` · **分支/Commit** `AI/task-966dcd0a` / `c939d3b` · **日期** `2026-08-07` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。已运行 `scan-all-rules.sh` 并将要点并入 §5，再写 LLM 结论。问题含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 21（inputs_content 列 `WebMvcConfig.java` 实际不存在，已跳过） |
| 变更行数 | `+1935 / -1` |

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| `LibraryBackendApplication` | `src/main/java/.../LibraryBackendApplication.java` | Spring Boot 启动类 |
| `ErrorCodeEnum` | `src/main/java/.../common/exception/ErrorCodeEnum.java` | 错误码枚举 |
| `GlobalExceptionHandler` | `src/main/java/.../common/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| `ServiceException` | `src/main/java/.../common/exception/ServiceException.java` | 业务异常 |
| `ApiResponse` | `src/main/java/.../common/response/ApiResponse.java` | 统一响应 |
| `DemoController` | `src/main/java/.../demo/controller/DemoController.java` | HelloWorld/Hash/BubbleSort 接口 |
| `ExportController` | `src/main/java/.../demo/controller/ExportController.java` | 导出接口 |
| `BubbleSortVO` | `src/main/java/.../demo/model/vo/BubbleSortVO.java` | 排序结果 VO |
| `HashVO` | `src/main/java/.../demo/model/vo/HashVO.java` | 哈希结果 VO |
| `HelloWorldVO` | `src/main/java/.../demo/model/vo/HelloWorldVO.java` | HelloWorld VO |
| `DemoService` | `src/main/java/.../demo/service/DemoService.java` | 演示服务接口 |
| `ExportService` | `src/main/java/.../demo/service/ExportService.java` | 导出服务接口 |
| `DemoServiceImpl` | `src/main/java/.../demo/service/impl/DemoServiceImpl.java` | 演示服务实现 |
| `ExportServiceImpl` | `src/main/java/.../demo/service/impl/ExportServiceImpl.java` | 导出服务实现 |
| `TrackAspect` | `src/main/java/.../track/aspect/TrackAspect.java` | 埋点切面 |
| `TrackController` | `src/main/java/.../track/controller/TrackController.java` | 统计查询接口 |
| `TrackRecordDO` | `src/main/java/.../track/model/entity/TrackRecordDO.java` | 埋点记录实体 |
| `TrackStatisticsVO` | `src/main/java/.../track/model/vo/TrackStatisticsVO.java` | 统计结果 VO |
| `TrackRecordRepository` | `src/main/java/.../track/repository/TrackRecordRepository.java` | 埋点数据访问层 |
| `TrackService` | `src/main/java/.../track/service/TrackService.java` | 埋点服务接口 |
| `TrackServiceImpl` | `src/main/java/.../track/service/impl/TrackServiceImpl.java` | 埋点服务实现 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 3 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: HelloWorld 接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 后端启动 When 调用 GET /api/demo/helloworld Then 返回欢迎消息 | ✅ | "分别写三个接口helloworld" | `DemoController.java:39-43`, `DemoServiceImpl.java:36-41` | 返回 `HelloWorldVO{message, timestamp}` |

### REQ-2: 哈希算法接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 输入字符串 When 调用 GET /api/demo/hash?input=xxx Then 返回SHA-256哈希 | ✅ | "哈希算法" | `DemoController.java:51-55`, `DemoServiceImpl.java:43-59` | 使用 `MessageDigest.getInstance("SHA-256")` |

### REQ-3: 冒泡排序接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 逗号分隔数字 When 调用 GET /api/demo/bubble-sort?input=3,1,2 Then 返回排序结果 | ✅ | "冒泡排序" | `DemoController.java:63-67`, `DemoServiceImpl.java:61-111` | 经典冒泡升序，含早停优化 |

### REQ-4: 导出接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 页面展示结果 When 调用 GET /api/demo/export?type=hash&input=xxx Then 下载CSV | ✅ | "后台提供导出接口，支持导出各个页面的展示结果" | `ExportController.java:38-45`, `ExportServiceImpl.java:40-62` | 支持三种类型 CSV，含 UTF-8 BOM |

### REQ-5: 埋点切面

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 接口被调用 When AOP切面拦截 Then 记录调用人和调用次数 | ✅ | "后端再做个埋点，获取调用次数和调用人" | `TrackAspect.java:41-80`, `TrackServiceImpl.java:45-60` | `@Around` 拦截 demo.controller，从请求头获取用户维度信息 |

### REQ-6/7: 统计可视化

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 维度+图表类型 When 调用 GET /api/track/statistics Then 返回折线/饼/柱状图数据 | ⚠️ | "折线图以及饼图和柱状图不同展示形式" | `TrackController.java:37-43`, `TrackServiceImpl.java:62-110` | 接口设计完整，但 `TrackRecordRepository` 存在编译错误导致功能不可用（见 P0-1） |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | `A3.4` `TrackRecordRepository.java:37` — 行宽超限（预扫命中，该行 SQL 注解较长） |
| ✅ | 其余 A1–A7 无违反 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 | `G16.2` 预扫 5 处全部误报（所有 catch 块均有日志）；`M016` `LocalDateTime.now()` 未指定时区 |
| 安全 | `security-checklist.md` S1–S10 | ❌ | P0 | `S1.1` `TrackRecordRepository.java:25-27,37-39` SQL 列名拼接 + 编译错误；`S10.2` 无 CORS 配置 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | 预扫 52/222 条无真阳性；LLM 逐文件审查未发现额外 Blocker |

> **预扫结果摘要**（`scan-all-rules.sh`）：
> ```
> P0=5 (G16.2 全部误报), P1=2 (M016), P2=1 (A3.4)
> Summary: 8 findings (P0=5, P1=2, P2=1) | 52/222 rules scanned
> ```

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则 |

---

## 7. 结论

- **合并建议**：**阻止合并**（存在 P0 编译错误，代码无法通过编译）
- **P0**：1. `TrackRecordRepository.java:25-27,37-39` — `@Query` 注解中 `+ :dimension +` 字符串拼接 SQL 列名，`:dimension` 非合法 Java 表达式，**编译错误**。且违反 S1.1 SQL 注入防御纵深原则。
- **P1/P2**：
  1. P1 `M016` `DemoServiceImpl.java:39` + `TrackServiceImpl.java:56` — `LocalDateTime.now()` 未指定时区
  2. P1 `S10.2` 无 CORS 配置，inputs_content 列出的 `WebMvcConfig.java` 实际不存在，前端跨域调用将失败
  3. P2 `A3.4` `TrackRecordRepository.java:37` — 行宽超限
  4. P2 `TrackAspect.java:70` — 埋点同步保存，注释已提及"生产环境可改为异步队列"，高并发下可能影响接口响应时间
- **一句话**：功能需求全部实现且结构清晰，但 `TrackRecordRepository` 的 SQL 拼接写法是编译错误级 P0，必须修复后才能合并；其余为时区/CORS 可靠性隐患。

---

## 7.1 问题片段（必填）

### P0-1: `TrackRecordRepository.java:25-29` — SQL 列名拼接编译错误 + 注入风险

- **P0** `S1.1` `src/main/java/com/antdigital/library/track/repository/TrackRecordRepository.java:25-29` — `@Query` 注解中通过 `+ :dimension +` 拼接 SQL 列名。`:dimension` 是 JPA 命名参数占位符，**不是合法的 Java 标识符**，在字符串拼接上下文中无法编译。即使改为合法 Java 变量拼接，也将引入 SQL 注入（列名不能用参数绑定）。
  片段范围：`src/main/java/com/antdigital/library/track/repository/TrackRecordRepository.java:24-30`

```java
L24|    /**
L25|     * 按指定维度聚合统计调用次数。
L26|     *
L27|     * @param dimension 维度字段名（user_type / user_level / user_department / user_id）
L28|     * @return 聚合结果列表
L29|     */
L30|    @Query(value = "SELECT t." + :dimension + " AS dimension_value, COUNT(*) AS call_count "
L31|            + "FROM track_record t GROUP BY t." + :dimension
L32|            + " ORDER BY call_count DESC",
L33|            nativeQuery = true)
L34|    List<Object[]> countByDimension(@Param("dimension") String dimension);
```

> **修复方向**：改用 `EntityManager` + `CriteriaBuilder` 动态构造，或为每个维度定义独立方法（4 个 `@Query` 硬编码列名），白名单在 Service 层已具备。

### P0-1b: 同一文件第二处 `TrackRecordRepository.java:37-41`

- **P0** `S1.1` `src/main/java/com/antdigital/library/track/repository/TrackRecordRepository.java:37-41` — 同样的 `+ :dimension +` 拼接，同样编译错误。
  片段范围：`src/main/java/com/antdigital/library/track/repository/TrackRecordRepository.java:36-42`

```java
L36|    /**
L37|     * 按日期和维度聚合统计调用次数（用于折线图）。
L38|     *
L39|     * @param dimension 维度字段名
L40|     * @return 聚合结果列表 [date, dimensionValue, count]
L41|     */
L42|    @Query(value = "SELECT DATE(t.call_time) AS call_date, t." + :dimension + " AS dimension_value, COUNT(*) AS call_count "
L43|            + "FROM track_record t GROUP BY DATE(t.call_time), t." + :dimension
L44|            + " ORDER BY call_date, dimension_value",
L45|            nativeQuery = true)
L46|    List<Object[]> countByDateAndDimension(@Param("dimension") String dimension);
```

### P1-1: `DemoServiceImpl.java:37-41` — LocalDateTime 未指定时区

- **P1** `M016` `src/main/java/com/antdigital/library/demo/service/impl/DemoServiceImpl.java:37-41` — `LocalDateTime.now()` 使用系统默认时区，多时区部署时时间戳不一致。
  片段范围：`src/main/java/com/antdigital/library/demo/service/impl/DemoServiceImpl.java:36-42`

```java
L36|    @Override
L37|    public HelloWorldVO helloWorld() {
L38|        logger.info("helloWorld 方法被调用");
L39|        String timestamp = LocalDateTime.now().format(FORMATTER);
L40|        return new HelloWorldVO("Hello, World! Welcome to Library Backend.", timestamp);
L41|    }
```

### P1-2: `TrackServiceImpl.java:55-57` — LocalDateTime 未指定时区

- **P1** `M016` `src/main/java/com/antdigital/library/track/service/impl/TrackServiceImpl.java:55-57` — 埋点 `callTime` 同样使用默认时区。
  片段范围：`src/main/java/com/antdigital/library/track/service/impl/TrackServiceImpl.java:54-58`

```java
L54|        record.setUserDepartment(userDepartment);
L55|        record.setCallTime(LocalDateTime.now());
L56|
L57|        trackRecordRepository.save(record);
```

### P1-3: 无 CORS 配置文件

- **P1** `S10.2` `src/main/java/com/antdigital/library/common/config/` — inputs_content 列出 `WebMvcConfig.java` 但该文件实际不存在（`common/` 下仅有 `exception/` 和 `response/`）。前端 `library-frontend` 与后端不同源（Vite dev server :5173 vs Spring :8080），无 CORS 配置将导致跨域请求被浏览器拦截。
  片段范围：`N/A(非 Java，文件缺失)`

### P2-1: `TrackAspect.java:67-79` — 埋点同步保存

- **P2** `G10.1` `src/main/java/com/antdigital/library/track/aspect/TrackAspect.java:67-79` — 埋点在主请求线程同步保存 DB，高并发时影响接口 RT。代码注释已提及"生产环境可改为异步队列"。
  片段范围：`src/main/java/com/antdigital/library/track/aspect/TrackAspect.java:67-80`

```java
L67|        // 执行目标方法
L68|        Object result = joinPoint.proceed();
L69|
L70|        // 异步保存埋点（此处同步保存，避免吞异常；生产环境可改为异步队列）
L71|        try {
L72|            trackService.saveTrackRecord(apiPath, userId, userName, userType, userLevel, userDepartment);
L73|        } catch (Exception e) {
L74|            // 埋点失败不影响业务流程
L75|            logger.error("埋点保存失败, apiPath: {}, userId: {}, errorMessage: {}",
L76|                    apiPath, userId, e.getMessage(), e);
L77|        }
L78|
L79|        return result;
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/java/com/antdigital/library/track/repository/TrackRecordRepository.java:25-29` — 将 `countByDimension` 的 `@Query` 改为 4 个硬编码列名的独立方法（`countByUserType` / `countByUserLevel` / `countByUserDepartment` / `countByUserId`），或改用 `EntityManager` + `CriteriaBuilder` 动态构造，消除 `+ :dimension +` 拼接
- [ ] **P0** `src/main/java/com/antdigital/library/track/repository/TrackRecordRepository.java:37-41` — 将 `countByDateAndDimension` 同上方式修复

### P1

- [ ] **P1** `src/main/java/com/antdigital/library/demo/service/impl/DemoServiceImpl.java:39` — 将 `LocalDateTime.now()` 改为 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))` 或使用 `Instant`
- [ ] **P1** `src/main/java/com/antdigital/library/track/service/impl/TrackServiceImpl.java:56` — 同上，`setCallTime` 指定显式时区
- [ ] **P1** `S10.2` — 新增 `src/main/java/com/antdigital/library/common/config/WebMvcConfig.java`，配置 `addCorsMappings` 允许前端源（或使用 `@CrossOrigin` 注解）

### P2（可选）

- [ ] **P2** `src/main/java/com/antdigital/library/track/repository/TrackRecordRepository.java:37` — 拆行降低 `@Query` 注解行宽
- [ ] **P2** `src/main/java/com/antdigital/library/track/aspect/TrackAspect.java:70` — 将埋点保存改为 `@Async` 异步执行（需配合 `@EnableAsync`）

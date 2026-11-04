# Code Review Checklist

> **Change** `bubble-sort` · **分支/Commit** `AI/task-DEV-0db410f7…b5db25` / `3269761` · **日期** `2026-11-04`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已在进入逐文件审查前，对变更路径运行 `references/script/scan-all-rules.sh`，输出贴入 Step 3 / Step 4 备注；随后由 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

**自动化预扫输出（原样）**

```text
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java/com/library/backend/controller/BubbleSortController.java src/main/java/com/library/backend/service/BubbleSortService.java src/main/java/com/library/backend/dto/ApiRequest.java src/main/java/com/library/backend/dto/ExportRequest.java src/test/java/com/library/backend/controller/BubbleSortControllerTest.java src/test/java/com/library/backend/service/BubbleSortServiceTest.java
Engine:  ripgrep


=== No findings. 52/222 rules scanned ===
```

**补充静态检查（本次手工执行）**

| 检查 | 命令/手段 | 结果 |
|------|-----------|------|
| 扫描脚本预扫 | `scan-all-rules.sh`（6 个文件） | 52/222 规则，No findings |
| 行宽 ≤120 / Tab / 行尾空白 | Python 按 Unicode 字符计数遍历 6 个文件 | 未命中（0 处） |
| `@Valid` / `@Validated` 使用 | `rg "@Valid\|@Validated" src pom.xml` | 0 处（仅 `spring-boot-starter-test` 命中） |
| 动态验证（编译/单测） | `command -v java mvn` | 不可用（本环境无 JDK/Maven），本次 CR 未执行运行时验证；引用 `.agents/system.changes/code.md` 记录的 `mvn test`（12 tests, 0 failures） |

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。**Bug 模式（B/M/I）** 不在本表分列，在下方 **§4.1** 按清单 ID 核销。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `src/main/java/com/library/backend/controller/BubbleSortController.java` | REQ-1/2/4 接口层 | ✅ | ✅ | N/A(无并发) | N/A(无写接口) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | ✅ | N/A(无外部调用) | ⚠️ | ⚠️ | N/A(无资金) | N/A(无监控变更) | ✅ | N/A(无表结构变更) | ✅ | N/A(无开关需求) | N/A(无SQL) | ✅ | N/A(无外部URL) | ✅ | N/A(无XML) | ✅ | N/A(无文件) | N/A(既有设计) | ✅ | N/A(未改CORS) | ⚠️ 已审有问题 |
| 2 | `src/main/java/com/library/backend/service/BubbleSortService.java` | REQ-3 排序算法 | ✅ | ✅ | N/A(无并发) | N/A(无写接口) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | N/A(无IO) | N/A(无外部调用) | ✅ | ⚠️ | N/A(无资金) | N/A(无监控变更) | ✅ | N/A(无表结构变更) | ✅ | N/A(无开关需求) | N/A(无SQL) | N/A(无HTML输出) | N/A(无外部URL) | ✅ | N/A(无XML) | N/A(无反序列化) | N/A(无文件) | N/A(既有设计) | ✅ | N/A(未改CORS) | ⚠️ 已审有问题 |
| 3 | `src/main/java/com/library/backend/dto/ApiRequest.java` | REQ-1 请求 DTO | ✅ | ✅ | N/A(无并发) | N/A(无写接口) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | N/A(无IO) | N/A(无外部调用) | ⚠️ | ✅ | N/A(无资金) | N/A(无监控变更) | N/A(无时间字段) | N/A(无表结构变更) | N/A(无日志) | N/A(无开关需求) | N/A(无SQL) | N/A(无HTML输出) | N/A(无外部URL) | N/A(无命令) | N/A(无XML) | ✅ | N/A(无文件) | N/A(既有设计) | ✅ | N/A(未改CORS) | ⚠️ 已审有问题 |
| 4 | `src/main/java/com/library/backend/dto/ExportRequest.java` | 编译一致性修复（jakarta 包名） | ✅ | ✅ | N/A(无并发) | N/A(无写接口) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | N/A(无IO) | N/A(无外部调用) | ✅ | ✅ | N/A(无资金) | N/A(无监控变更) | N/A(无时间字段) | N/A(无表结构变更) | N/A(无日志) | N/A(无开关需求) | N/A(无SQL) | N/A(无HTML输出) | N/A(无外部URL) | N/A(无命令) | N/A(无XML) | ✅ | N/A(无文件) | N/A(既有设计) | ✅ | N/A(未改CORS) | ✅ 已审 |
| 5 | `src/test/java/com/library/backend/controller/BubbleSortControllerTest.java` | REQ-1/2/4 验证 | ✅ | ✅ | N/A(无并发) | N/A(无写接口) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | N/A(无IO) | N/A(无外部调用) | ✅ | ⚠️ | N/A(无资金) | N/A(无监控变更) | N/A(无时间字段) | N/A(无表结构变更) | N/A(无日志) | N/A(无开关需求) | N/A(无SQL) | N/A(无HTML输出) | N/A(无外部URL) | N/A(无命令) | N/A(无XML) | N/A(无反序列化) | N/A(无文件) | N/A(既有设计) | N/A(无密钥) | N/A(未改CORS) | ⚠️ 已审有问题 |
| 6 | `src/test/java/com/library/backend/service/BubbleSortServiceTest.java` | REQ-3 验证 | ✅ | ✅ | N/A(无并发) | N/A(无写接口) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | N/A(无IO) | N/A(无外部调用) | ✅ | ✅ | N/A(无资金) | N/A(无监控变更) | N/A(无时间字段) | N/A(无表结构变更) | N/A(无日志) | N/A(无开关需求) | N/A(无SQL) | N/A(无HTML输出) | N/A(无外部URL) | N/A(无命令) | N/A(无XML) | N/A(无反序列化) | N/A(无文件) | N/A(既有设计) | N/A(无密钥) | N/A(未改CORS) | ✅ 已审 |

- 由 `git show --stat HEAD` 展开；非 Java 文件 `pom.xml`、`.agents/system.changes/code.md` 标 `跳过(非 Java)`。
- 守卫：本次含 6 个 `.java` 文件，满足 Java 守卫，继续审查。
- 收口：6 个文件各 `Sn/Gn` 列均非 `⬜`，与下方 Step 4 逐条 ID 表一致。

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 请求体 `{"array":[5,3,8,1,2]}` When POST `/api/bubble-sort` Then 返回 200 且含排序结果 | `.agents/system.changes/design.md` §4.3「**路径**: `POST /api/bubble-sort`；**请求体**: `{"array": [5, 3, 8, 1, 2]}`」 | `BubbleSortController.java`；`ApiRequest.java` | ✅ | `BubbleSortController.java:41-57`；`BubbleSortControllerTest.java:28-44` |
| REQ-2 | Given 排序成功 When 读取响应体 Then 结构为 `{code,message,data:{original,sorted,timestamp}}` | 同 §4.3「**响应体**: `{"code":200,"message":"success","data":{"original":…,"sorted":…,"timestamp":"2026-08-06T10:00:00Z"}}`」 | `BubbleSortController.java` | ⚠️ | `BubbleSortController.java:51-56`；`timestamp` 取 `Instant.now().toString()` → `2026-11-04T01:47:57.940123Z`（含小数秒），与系分示例秒级格式不同（P2，见 report REQ-2） |
| REQ-3 | Given 任意整数数组（含负数/重复/空/单元素） When 调用排序 Then 返回升序结果且不改动入参 | 需求「实现一个冒泡排序」+ §4.3 `sorted` 字段语义 | `BubbleSortService.java` | ✅ | `BubbleSortService.java:25-50`；`BubbleSortServiceTest.java:21-111`（8 例，边界齐全） |
| REQ-4 | Given `array` 缺失或元素为 `null` When 调用接口 Then 返回 400 参数错误 | §4.3 未定义错误契约；`.agents/system.changes/code.md` L166-170 记录「array 缺失 → 业务码 400；元素 null → 400」 | `BubbleSortController.java`；`ApiRequest.java` | ⚠️ | `BubbleSortController.java:44-46` 返回 `ApiResponse.error(400)` 但 HTTP 仍为 200；元素 null（`:70-72`）经 `GlobalExceptionHandler:14-19` 返回 HTTP 400 —— 同类错误两套状态（P1，见 report G10.1） |

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7 逐节核销（扫描脚本 A* 项无命中；LLM 复核一致）：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=顶层类名；UTF-8；无 Tab（字符级检查 0 命中） |
| A2 | 源文件结构/import 顺序 | ✅ | 无 `import *`；`com/lombok/org` 与 `java.*` 分组，组内 ASCII 升序（`BubbleSortController.java:3-16`、`ApiRequest.java:3-10`） |
| A3 | 代码样式 | ✅ | K&R 大括号；4 空格缩进；Unicode 字符计数最长行 120 以内；`if (` 空格规范 |
| A4 | 命名规范 | ✅ | 类 UpperCamelCase、方法 lowerCamelCase、常量 `BAD_REQUEST_CODE`（`BubbleSortController.java:30`）、测试类 `XxxTest` |
| A5 | 编码实践 | ✅ | 无重写方法；无空 catch；无 `finalize` |
| A6 | 特定元素样式 | ✅ | 无数组声明式 C 风格；无 switch；无 long 字面量 |
| A7 | Javadoc 规范 | ✅ | 类/公开方法均有 Javadoc，`@param/@return/@throws` 顺序正确（`BubbleSortService.java:16-24`、`BubbleSortController.java:34-40`） |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫：`scan-all-rules.sh` → `No findings (52/222 rules)`；下表为脚本 + LLM 合并结论。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无 `parse/of` 字面量调用 |
| B002 | ✅ | 数组断言用 AssertJ `containsExactly`，无 `equals` 比较 |
| B003 | N/A | 无 `Arrays.fill` |
| B004 | N/A | 无数组 `toString()` |
| B005 | ✅ | `Arrays.asList(5,3,8,…)` 元素为 `Integer`，非基本类型数组 |
| B006 | N/A | 未用 JUnit `assertEquals` |
| B007 | N/A | 无 `catch (Throwable/Error)` |
| B008 | N/A | 无 `Executors` |
| B009 | N/A | 无移位运算 |
| B010 | N/A | 无 `BigDecimal` |
| B011 | ✅ | `==` 仅用于与 `null` 比较（`BubbleSortController.java:44,70`） |
| B012 | N/A | 无 `Calendar` |
| B013 | N/A | 无 `Calendar` |
| B014 | N/A | 无泛型不兼容的集合查询 |
| B015 | N/A | 无 `Collection.toArray` |
| B016 | N/A | 无 `Comparable` 实现 |
| B017 | N/A | 无 `this == null` |
| B018 | N/A | 无三目运算符 |
| B019 | N/A | 无 Money 类 |
| B020 | N/A | 无常量乘法 |
| B021 | N/A | 无 Jedis |
| B022 | N/A | 无 `SimpleDateFormat` |
| B023 | N/A | 无"创建未抛"的异常实例 |
| B024 | N/A | 无 Thread |
| B025 | N/A | 无双括号初始化 |
| B026 | ✅ | 判空统一用 `==`（`BubbleSortService.java:26`） |
| B027 | N/A | 未重写 `equals` |
| B028 | N/A | 无 `commons DateUtil` |
| B029 | N/A | 无 POJO setter（Lombok 生成） |
| B030 | N/A | 无浮点比较 |
| B031 | N/A | 无 `String.format` |
| B032 | N/A | 无注解反射 |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | ✅ | 无 `a && a` 式恒等表达式 |
| B036 | N/A | 无 IdentityHashMap |
| B037 | N/A | 无可变参数 |
| B038 | ✅ | 双循环无递归（`BubbleSortService.java:33-47`） |
| B039 | N/A | 无 `indexOf` |
| B040 | N/A | 无 `isInstance` |
| B041 | N/A | 无 JDBC |
| B042 | N/A | JUnit 5（非 JUnit3） |
| B043 | N/A | 无内部类测试 |
| B044 | N/A | 无 JUnit3/4 混用 |
| B045 | N/A | 无 `synchronized` |
| B046 | ✅ | `i`/`j` 均在循环体内自增 |
| B047 | N/A | 无 `compare` 调用 |
| B048 | N/A | 无 `Math.round` |
| B049 | N/A | 无日期格式化 |
| B050 | N/A | 无日期格式化 |
| B051 | N/A | 无 `Boolean.getBoolean` |
| B052 | N/A | 无 `YYYY` 格式 |
| B053 | ✅ | 异常断言用 `assertThatThrownBy`（`BubbleSortServiceTest.java:120`） |
| B054 | N/A | 无 EqualsTester |
| B055 | N/A | 无 Mockito |
| B056 | N/A | 未对 `Arrays.asList` 结果增删 |
| B057 | N/A | 无增强 for 修改集合 |
| B058 | N/A | 无集合自传入 |
| B059 | N/A | 无 `Collections.nCopies` |
| B060 | N/A | 无三目拆箱 |
| B061 | N/A | 无 `sun.misc` Base64 |
| B062 | N/A | 无 ClassLoader 强转 |
| B063 | N/A | 已将 `javax.validation` 改为 `jakarta.validation`（`ApiRequest.java:8-9`、`ExportRequest.java:8-9`） |
| B064 | N/A | 无 Optional |
| B065 | N/A | 无 POJO 自赋值 |
| B066 | N/A | 无 `Math.random` |
| B067 | N/A | 无 Random 取余 |
| B068 | N/A | 无自赋值 |
| B069 | N/A | 无 compareTo 自比较 |
| B070 | N/A | 无 equals 自比较 |
| B071 | N/A | 无 `size() >= 0` |
| B072 | N/A | 无 Stream.toString |
| B073 | N/A | 无 StringBuilder |
| B074 | N/A | 无 `substring(0)` |
| B075 | ✅ | `for (i=0; i<length-1; i++)`、`for (j=0; j<length-i-1; j++)` 条件与增量一致 |
| B076 | N/A | 无 `@Transactional` |
| B077 | N/A | 无 `catch (Throwable)` |
| B078 | N/A | 无 `assertThat(x).isEqualTo(x)` |
| B079 | N/A | 无 `@Mock` 字段 |
| B080 | ✅ | 12 个用例均含断言（`BubbleSortServiceTest` 8 例、`BubbleSortControllerTest` 4 例） |
| B081 | N/A | 无原地排序后丢弃集合 |
| M001 | N/A | 无重复条件分支 |
| M002 | N/A | 无 `instanceof` |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 无 `printStackTrace` |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无常量布尔表达式 |
| M007 | N/A | 无空 catch（含测试） |
| M008 | N/A | 未重写 `equals` |
| M009 | N/A | 无跨类型 `equals` |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally return |
| M013 | N/A | 无浮点强转 |
| M014 | N/A | 无枚举 `getClass` |
| M015 | N/A | 无继承字段隐藏 |
| M016 | ✅ | 使用 `Instant.now()`（无系统时区依赖，输出 UTC）`BubbleSortController.java:54` |
| M017 | N/A | 所有测试方法均有 `@Test` |
| M018 | N/A | 无显式加锁 |
| M019 | N/A | 无枚举 switch |
| M020 | N/A | 无重写方法 |
| M021 | N/A | 无 `equals(SpecificType)` |
| M022 | N/A | 无 `Optional.of(null)` |
| M023 | N/A | 无对象打印 |
| M024 | N/A | 无 `Optional.get` |
| M025 | N/A | 无 final 内 protected 成员 |
| M026 | N/A | 无 `@Mock static` |
| M027 | N/A | 无 ThreadLocal |
| I001 | ✅ | 异常断言同时校验消息（`BubbleSortControllerTest.java:85-88`） |
| I002 | N/A | 无 `@DoNotMock` 场景 |
| I003 | N/A | 无 AutoValue |
| I004 | ✅ | 未使用 `java.util.Date` |
| I005 | N/A | 无 JUnit3 |
| I006 | N/A | 无 `setUp()` |
| I007 | N/A | 无 `tearDown()` |
| I008 | N/A | 无 dataProvider |
| I009 | N/A | 仅统计用途，不构成问题 |
| I010 | N/A | 纯单测，未启动容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无事务内先读后写 |
| G1.2 | N/A | 无加锁更新 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源锁 |
| G2.1 | N/A | 无写接口（排序为纯计算） |
| G2.2 | N/A | 无重试/定时任务落库 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 无 `@Transactional` |
| G4.1 | N/A | 无 SQL |
| G4.2 | N/A | 无 SQL |
| G4.3 | N/A | 无 SQL |
| G4.4 | N/A | 参考清单未定义该 ID（模板占位） |
| G5.1 | N/A | 无 MQ 消费 |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 无 catch 吞异常（`BubbleSortController` 无 catch；异常交由 `GlobalExceptionHandler` 记录 WARN） |
| G8.2 | N/A | 无外部强依赖调用 |
| G8.3 | N/A | 无 I/O 资源 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无边界的 `Executors` 线程池 |
| G8.7 | N/A | 参考清单未定义该 ID（模板占位） |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | ⚠️ | **P1**：同类参数错误两套语义——`array` 缺失返回 HTTP 200 + body `code=400`（`BubbleSortController.java:44-46`），元素 `null` 抛 `IllegalArgumentException` → `GlobalExceptionHandler` 返回 HTTP 400（`BubbleSortController.java:70-72` + `GlobalExceptionHandler.java:14-19`） |
| G10.2 | ✅ | 契约与系分 §4.3 一致（路径/字段名未变） |
| G10.3 | N/A | 参考清单未定义该 ID（模板占位） |
| G11.1 | ✅ | 新增单测 12 例且均含断言；Controller 层未走 HTTP 栈的问题另见 G11.2 备注与 report P2 |
| G11.2 | ⚠️ | **P2**：未覆盖「最大值」边界——请求数组无长度上限（`BubbleSortController.java:43-49`），而算法为 O(n²)（`BubbleSortService.java:33-47`） |
| G11.3 | ✅ | `array == null` 显式 400（`:44-46`）；元素 `null` 显式抛参异常（`:70-72`）；Service `null` 抛 `IllegalArgumentException`（`BubbleSortService.java:26-28`） |
| G11.4 | ✅ | 仅整型比较与交换，无运算溢出/除零/浮点精度；无金额场景 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | ✅ | 变更文件无日志级别误用 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户表查询 |
| G14.3 | ✅ | `Instant.now().toString()` 输出 UTC，未存本地时区字符串（`BubbleSortController.java:54`） |
| G14.4 | N/A | 无日期格式化/解析 |
| G15.1 | N/A | 无表结构变更 |
| G15.2 | ✅ | 未改动既有接口签名，`ApiRequest.array` 字段保持兼容 |
| G15.3 | N/A | 无开关需求（纯新增能力） |
| G16.1 | N/A | 埋点由既有 `ApiCallAspect` 承担，非本次变更 |
| G16.2 | ✅ | 异常路径由 `GlobalExceptionHandler` 记录 message（既有实现，未引入回归） |
| G16.3 | ✅ | 参数异常 WARN、系统异常 ERROR（既有实现） |
| G16.4 | ✅ | 变更文件无空 catch / `printStackTrace` |
| G17.1 | N/A | 无功能开关需求 |
| G17.2 | N/A | 无外部依赖降级场景 |
| G17.3 | N/A | 无数据变更 |
| G18.1 | N/A | 参考清单未定义该 ID（模板占位） |
| G18.2 | N/A | 参考清单未定义该 ID（模板占位） |
| G18.3 | N/A | 参考清单未定义该 ID（模板占位） |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL |
| S1.2 | N/A | 无动态 SQL |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1 | ✅ | 响应为 Jackson 序列化的 JSON，无 HTML/JS 拼接 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 302 跳转 |
| S3.3 | N/A | 无外部请求 |
| S4.1 | N/A | 无系统命令拼接 |
| S4.2 | ✅ | 纯内存计算，未调用外部命令 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无 Java 反序列化 |
| S6.2 | ✅ | `@RequestBody ApiRequest` 为具体类型，未启用 `@JsonTypeInfo` 多态 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件路径处理 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | N/A | 项目整体无鉴权框架（既有设计，Demo 场景），非本变更引入；对外发布前需统一接入，见 report §7 范围说明 |
| S8.2 | ✅ | 排序为只读计算但使用 POST（`BubbleSortController.java:41`），未通过 GET 触发变更 |
| S8.3 | N/A | 无数据 ID |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | 无硬编码密钥/凭证 |
| S9.2 | ✅ | 错误消息仅含字段名，未泄露敏感信息 |
| S9.3 | N/A | 无传输/存储加密变更 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无会话态写操作 |
| S10.2 | N/A | 未改动 `CorsConfig`（既有配置，见 report §7 范围说明） |
| S10.3 | N/A | 无跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

> 项目已引入 `spring-boot-starter-validation`（`pom.xml` +6 行），且 `ApiRequest`/`ExportRequest` 已改用 `jakarta.validation` 注解，因此将 U1.1（团队工程约束示例项）作为**生效项**核销；U2 无配置，标 `N/A`。

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ⚠️ | **P1**：`rg "@Valid\|@Validated"` 全仓 0 命中 → `ApiRequest.userId/userType` 上的 `@NotBlank`/`@Pattern` 永不生效；且其"必填"语义与系分 §4.3 仅含 `array` 的请求体冲突（`ApiRequest.java:25-31`、`BubbleSortController.java:42`） |
| U1.2 | N/A | 未配置 |
| U1.3 | N/A | 未配置 |
| U2.1 | N/A | 未配置业务红线规则 |
| U2.2 | N/A | 未配置业务红线规则 |
| U2.3 | N/A | 未配置业务红线规则 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（含 N/A 原因）
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`

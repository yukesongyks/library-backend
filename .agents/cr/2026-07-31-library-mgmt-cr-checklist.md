# Code Review Checklist

> **Change** `编码实现 (stage: coding, round: 1)` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-054eddac-e43f-4076-9f4b-c0410ffd203c-` / `1ee5cda` · **日期** `2026-07-31`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：✅ 已运行 `references/script/scan-all-rules.sh`，输出见 Step 3/Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 1 | src/main/java/com/library/LibraryApplication.java | 启动类 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | src/main/java/com/library/common/api/Result.java | 统一返回 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 3 | src/main/java/com/library/common/enums/ResultCode.java | 错误码枚举 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 4 | src/main/java/com/library/common/exception/BusinessException.java | 业务异常 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 5 | src/main/java/com/library/common/exception/GlobalExceptionHandler.java | 全局异常处理 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ⚠️ |
| 6 | src/main/java/com/library/common/generator/SnowflakeIdGenerator.java | ID生成器 | ✅ | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |
| 7 | src/main/java/com/library/config/JpaAuditConfig.java | JPA审计配置 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 8 | src/main/java/com/library/controller/BookController.java | REST接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ⚠️ |
| 9 | src/main/java/com/library/controller/vo/BookPageQuery.java | 分页查询VO | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 10 | src/main/java/com/library/dto/BookRequest.java | 请求DTO | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 11 | src/main/java/com/library/dto/BookResponse.java | 响应DTO | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 12 | src/main/java/com/library/dto/PageResult.java | 分页结果DTO | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 13 | src/main/java/com/library/entity/Book.java | 图书实体 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 14 | src/main/java/com/library/repository/BookRepository.java | DAO层 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 15 | src/main/java/com/library/service/BookService.java | 服务接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 16 | src/main/java/com/library/service/impl/BookServiceImpl.java | 服务实现 | ✅ | ⚠️ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |
| 17 | src/test/java/com/library/service/impl/BookServiceImplTest.java | 单元测试 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 图书列表 When 分页查询 Then 返回分页结果 | 「实现一个简单的图书管理系统」-图书列表 | BookController.java:42, BookServiceImpl.java:40, BookServiceImplTest.java:69 | ✅ | `listBooks(pageNum,pageSize)` 含默认值/上限/排序，测试覆盖 |
| REQ-2 | Given 图书ID When 查询 Then 返回图书或不存在异常 | 「简单的图书管理系统」-查询单本 | BookController.java:51, BookServiceImpl.java:67 | ✅ | `getBookById` + `BOOK_NOT_FOUND` 异常，测试覆盖 |
| REQ-3 | Given 图书信息 When 新增 Then 创建并返回 | 「简单的图书管理系统」-新增图书 | BookController.java:60, BookServiceImpl.java:74 | ✅ | `createBook` + ISBN唯一性校验 + @Valid，测试覆盖 |
| REQ-4 | Given 图书ID+新信息 When 更新 Then 更新并返回 | 「简单的图书管理系统」-编辑图书 | BookController.java:69, BookServiceImpl.java:96 | ✅ | `updateBook` + ISBN变更校验，测试覆盖 |
| REQ-5 | Given 图书ID When 删除 Then 删除 | 「简单的图书管理系统」-删除图书 | BookController.java:79, BookServiceImpl.java:121 | ✅ | `deleteBook` + 存在性校验，测试覆盖 |
| REQ-6 | Given 重复ISBN When 新增/更新 Then 拒绝并提示 | 「简单的图书管理系统」-数据一致性 | BookServiceImpl.java:76,102 | ✅ | `findByIsbn` 唯一性校验 + `BOOK_ISBN_DUPLICATED` |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 编码UTF-8，换行一致 |
| A2 | 源文件结构/import 顺序 | ✅ | package→import→类，import 按字母序 |
| A3 | 代码样式 | ✅ | 缩进4空格，大括号规范 |
| A4 | 命名规范 | ✅ | 类名/方法名/常量命名规范 |
| A5 | 编码实践 | ⚠️ | BookServiceImpl.java:82-88 手工逐字段赋值，可用 BeanUtils；BookResponse.of 手工映射同理（P2建议） |
| A6 | 特定元素样式 | ✅ | 枚举常量大写，常量全大写下划线 |
| A7 | Javadoc 规范 | ✅ | 各公共类/方法均有 Javadoc + @author |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫 `scan-all-rules.sh` 输出：`[P1] M016 — JavaTimeDefaultTimeZone: src/test/java/.../BookServiceImplTest.java:63,64`

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无 SimpleDateFormat（非时间格式化场景） |
| B002 | N/A | 无任意类型反射调用 |
| B003 | N/A | 无 JNDI 注入 |
| B004 | N/A | 无 LDAP 注入 |
| B005 | N/A | 无 ScriptEngine 执行 |
| B006 | N/A | 无不安全反序列化 |
| B007 | N/A | 无 XPath 处理 |
| B008 | N/A | 无 XML 实体扩展 |
| B009 | N/A | 无 regex DoS 源自外部输入（ISBN pattern 非用户可控 DoS 向量） |
| B010 | N/A | 无 native 序列化（implements Serializable 为标记接口） |
| B011 | N/A | 无 Finalizer |
| B012 | N/A | 无 `System.exit` |
| B013 | N/A | 无 `Runtime.exec` |
| B014 | N/A | 无资源未关闭（无 Closeable 手工管理） |
| B015 | N/A | 无 ThreadLocal 泄露 |
| B016 | N/A | 无自定义 ClassLoader |
| B017 | N/A | 无不安全 SSL/TLS |
| B018 | N/A | 无 URL 重定向至外部输入（无 redirect） |
| B019 | N/A | 无文件路径拼接外部输入 |
| B020 | N/A | 无路径遍历 |
| B021 | N/A | 无命令注入 |
| B022 | N/A | 无 XXE |
| B023 | N/A | 无不安全 XPath |
| B024 | N/A | 无 SQL 语句拼接（全用 Spring Data JPA 方法名） |
| B025 | N/A | 无 `Statement.execute` |
| B026 | N/A | 无 Hibernate HQL 拼接 |
| B027 | N/A | 无 `Class.forName` 外部输入 |
| B028 | N/A | 无 `Thread.sleep` 业务路径 |
| B029 | N/A | 无 `instanceof` 链 |
| B030 | N/A | 无异常被忽略 |
| B031 | N/A | 无 `return` 吞异常 |
| B032 | N/A | 无 `Optional.get` 无 isPresent |
| B033 | N/A | 无 `assert` 业务校验 |
| B034 | N/A | 无魔法值散布（常量已提取） |
| B035 | N/A | 无硬编码密码 |
| B036 | N/A | 无调试 print |
| B037 | N/A | 无 `e.printStackTrace()` |
| B038 | N/A | 无静态可变集合非 final |
| B039 | N/A | 无 `Integer` == 比较 |
| B040 | N/A | 无浮点 == 比较 |
| B041 | N/A | 无字符串 == 比较 |
| B042 | N/A | 无 `Boolean` 对象构造 |
| B043 | N/A | 无集合容量未指定 |
| B044 | N/A | 无 `new String()` |
| B045 | N/A | 无空 catch 无日志 |
| B046 | N/A | 无 NPE 风险的拆箱（stock 为 Integer，直接 set 无拆箱） |
| B047 | N/A | 无 Calendar 获取 |
| B048 | N/A | 无 Date 弃用方法 |
| B049 | N/A | 无 `Math.random` |
| B050 | N/A | 无 `UUID.randomUUID` 安全场景 |
| B051 | N/A | 无 `SecureRandom` 误用 |
| B052 | N/A | 无 `MessageDigest` 单例 |
| B053 | N/A | 无正则编译未复用（Pattern 为注解内联，框架管理） |
| B054 | N/A | 无 `String.format` 大量调用 |
| B055 | N/A | 无 Stream peek 副作用 |
| B056 | N/A | 无 `parallelStream` 共享可变 |
| B057 | N/A | 无 `collect` 误用 |
| B058 | N/A | 无 `forEach` 修改集合 |
| B059 | N/A | 无 `subList` 持有 |
| B060 | N/A | 无 `toArray` 类型不匹配 |
| B061 | N/A | 无 `Collections.synchronized` 迭代未同步 |
| B062 | N/A | 无 `ConcurrentHashMap` 复合操作 |
| B063 | N/A | 无 `HashMap` 多线程 |
| B064 | N/A | 无 `ArrayList` 多线程 |
| B065 | N/A | 无迭代器并发修改 |
| B066 | N/A | 无枚举单例可变状态 |
| B067 | N/A | 无 `Thread.interrupted` 误用 |
| B068 | N/A | 无 `Object.wait` 无循环 |
| B069 | N/A | 无 `notify` 而非 `notifyAll` |
| B070 | N/A | 无锁顺序不一致 |
| B071 | N/A | 无双重检查锁未 volatile |
| B072 | N/A | 无 `synchronized(this)` |
| B073 | N/A | 无 `synchronized(String)` |
| B074 | N/A | 无 `synchronized(Integer)` |
| B075 | N/A | 无 `synchronized` 非final字段 |
| B076 | N/A | 无死锁风险 |
| B077 | N/A | 无自旋锁 |
| B078 | N/A | 无忙等待 |
| B079 | N/A | 无 `Thread.yield` |
| B080 | N/A | 无 `Thread.stop` |
| B081 | N/A | 无 `Thread.destroy` |
| M001 | N/A | 无 SimpleDateFormat 共享 |
| M002 | N/A | 无 Calendar 线程不安全 |
| M003 | N/A | 无 Date 线程不安全 |
| M004 | N/A | 无 Joda-Time |
| M005 | N/A | 无 `Date.before/after` |
| M006 | N/A | 无 `Date.getTime` 零判断 |
| M007 | N/A | 无 `System.currentTimeMillis` 多次调用比较 |
| M008 | N/A | 无 `Math.abs` 负数边界 |
| M009 | N/A | 无 `Integer.MIN_VALUE` 取反 |
| M010 | N/A | 无位运算优先级 |
| M011 | N/A | 无移位负数 |
| M012 | N/A | 无 `Random` 安全场景 |
| M013 | N/A | 无 `SecureRandom` 种子 |
| M014 | N/A | 无 `UUID` 版本 |
| M015 | N/A | 无 `String.length` 误判字符 |
| M016 | ❌ | 预扫命中：`BookServiceImplTest.java:63,64` — `LocalDateTime.now()` 使用系统默认时区，测试时间不确定。LLM复核确认 |
| M017 | N/A | 无 `Instant` 精度 |
| M018 | N/A | 无 `Duration` 负数 |
| M019 | N/A | 无 `Period` 误用 |
| M020 | N/A | 无 `ZoneId` 硬编码 |
| M021 | N/A | 无 `OffsetDateTime` 误用 |
| M022 | N/A | 无 `ZonedDateTime` 误用 |
| M023 | N/A | 无 `Clock` 误用 |
| M024 | N/A | 无时间字符串解析 |
| M025 | N/A | 无时间格式化输出 |
| M026 | N/A | 无时间比较 |
| M027 | N/A | 无时间加减溢出 |
| I001 | N/A | 无 todo 注释残留 |
| I002 | N/A | 无 debug 断点 |
| I003 | N/A | 无 commented code |
| I004 | N/A | 无 magic number（已提取常量） |
| I005 | N/A | 无 overly complex method |
| I006 | N/A | 无 long parameter list |
| I007 | N/A | 无 large class |
| I008 | N/A | 无 feature envy |
| I009 | N/A | 无 data class（DTO 有方法） |
| I010 | N/A | 无 dead code |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | ⚠️ | SnowflakeIdGenerator:34 `synchronized` + :23 `AtomicLong` 混用，双重同步机制冗余但不致错 |
| G1.2 | ✅ | SnowflakeIdGenerator:36 时钟回拨有检查并抛异常 |
| G1.3 | N/A | 无显式锁 |
| G1.4 | N/A | 无线程池 |
| G2.1 | N/A | 无超时调用外部 |
| G2.2 | N/A | 无重试逻辑 |
| G2.3 | N/A | 无限流 |
| G3.1 | N/A | 无手工 IO/Closeable |
| G3.2 | N/A | 无连接池 |
| G4.1 | ⚠️ | BookServiceImpl:73,95,120 `@Transactional(rollbackFor=Exception.class)` 正确；但 createBook 的 ISBN 查+插非原子，高并发下重复 ISBN 可能绕过应用层校验（DB unique 约束兜底，Book.java:55） |
| G4.2 | N/A | 无显式 propagation |
| G4.3 | N/A | 无只读事务标注（listBooks/getBookById 未标 @Transactional(readOnly=true)，P2建议） |
| G4.4 | N/A | 无事务传播复杂场景 |
| G5.1 | N/A | 无外部缓存 |
| G6.1 | N/A | 无 MQ |
| G6.2 | N/A | 无 MQ |
| G7.1 | N/A | 无定时任务 |
| G7.2 | N/A | 无定时任务 |
| G8.1 | ⚠️ | BookServiceImpl:100 `book.getIsbn().equals(request.getIsbn())` — `book.getIsbn()` 理论上来自 DB 非空(nullable=false)，但未用 `Objects.equals` 防御 |
| G8.2 | ✅ | listBooks:41-44 分页参数 null/<1 边界处理有默认值 |
| G8.3 | ✅ | stock 校验 `@PositiveOrZero` |
| G8.4 | ✅ | PageRequest.of(actualPageNum-1, ...) 越界由 Spring Data 处理 |
| G8.5 | N/A | 无集合空判断风险（stream + collect） |
| G8.6 | N/A | 无数组越界 |
| G8.7 | N/A | 无除零 |
| G9.1 | N/A | 无 RPC |
| G9.2 | N/A | 无 RPC |
| G9.3 | N/A | 无 RPC |
| G10.1 | N/A | 无灰度 |
| G10.2 | N/A | 无灰度 |
| G10.3 | N/A | 无灰度 |
| G11.1 | N/A | 无监控埋点（简单系统） |
| G11.2 | N/A | 无监控 |
| G11.3 | N/A | 无监控 |
| G11.4 | N/A | 无监控 |
| G12.1 | N/A | 无容量评估 |
| G12.2 | N/A | 无容量 |
| G13.1 | N/A | 无依赖降级 |
| G14.1 | ⚠️ | SnowflakeIdGenerator:37 `throw new RuntimeException(...)` 未带 cause，且应优先用 HibernateException 或自定义异常（P1） |
| G14.2 | ✅ | GlobalExceptionHandler 兜底 `Exception.class` 有 |
| G14.3 | N/A | 无熔断 |
| G14.4 | N/A | 无降级 |
| G15.1 | N/A | 无配置 |
| G15.2 | N/A | 无配置 |
| G15.3 | N/A | 无配置 |
| G16.1 | ✅ | 业务异常有 log.warn |
| G16.2 | ✅ | 系统异常有 log.error + e |
| G16.3 | ✅ | 关键操作有 log.info |
| G16.4 | ✅ | SnowflakeIdGenerator:37 时钟回拨仅 throw 无日志（P2建议加 log） |
| G17.1 | N/A | 无预案 |
| G17.2 | N/A | 无预案 |
| G17.3 | N/A | 无回滚脚本 |
| G18.1 | ⚠️ | application.yml:15 `h2.console.enabled=true` 生产环境暴露 DB 控制台（P1） |
| G18.2 | ⚠️ | application.yml:19 `ddl-auto=update` 生产不应自动改表结构（P1） |
| G18.3 | ⚠️ | application.yml:12 `password:` 空 + :11 `username=sa` 默认凭据（简单系统可接受，P2参考） |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | 无 SQL 拼接，全 Spring Data JPA 方法名查询 |
| S1.2 | ✅ | 无原生 JDBC |
| S1.3 | N/A | 无 MyBatis |
| S2.1 | N/A | 无认证（简单系统，未要求） |
| S2.2 | N/A | 无认证 |
| S2.3 | N/A | 无认证 |
| S3.1 | N/A | 无鉴权 |
| S3.2 | N/A | 无鉴权 |
| S3.3 | N/A | 无鉴权 |
| S4.1 | ✅ | BookRequest 有 @NotBlank/@Size/@Pattern/@NotNull/@PositiveOrZero 输入校验 |
| S4.2 | ✅ | Controller 有 @Valid |
| S5.1 | N/A | 无密钥（H2 空密码，简单系统） |
| S5.2 | N/A | 无密钥 |
| S6.1 | N/A | 无敏感日志（ISBN/title 非敏感） |
| S6.2 | N/A | 无敏感 |
| S6.3 | N/A | 无敏感 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件 |
| S7.3 | N/A | 无文件 |
| S8.1 | N/A | 无 SSRF（无出站请求） |
| S8.2 | N/A | 无 SSRF |
| S8.3 | N/A | 无 SSRF |
| S8.4 | N/A | 无 SSRF |
| S9.1 | ⚠️ | BookController:42 `listBooks(BookPageQuery query)` 直接对象绑定，无 @Valid 但 VO 无校验注解；pageNum/pageSize 无上限校验注解，依赖 service 兜底（P2参考） |
| S9.2 | N/A | 无 XML |
| S9.3 | N/A | 无 YAML 外部输入 |
| S9.4 | N/A | 无 JSON 反序列化类型 |
| S10.1 | N/A | 无 CSRF（纯 API） |
| S10.2 | N/A | 无 CORS 配置（简单系统） |
| S10.3 | N/A | 无跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> 按 `customized-checklist.md` 逐条核销。

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A(示例项) | 清单为示例项，未启用自定义规则 |
| U1.2 | N/A(示例项) | 同上 |
| U1.3 | N/A(示例项) | 同上 |
| U2.1 | N/A(示例项) | 同上 |
| U2.2 | N/A(示例项) | 同上 |
| U2.3 | N/A(示例项) | 同上 |

> **整节：N/A(未启用自定义规则)** — customized-checklist.md 全为示例占位项。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`

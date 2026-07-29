# 图书管理系统 代码审查报告 (CR Report)

> **文档元信息**
>
> | 项目 | 内容 |
> |------|------|
> | 审查日期 | 2026-07-29 |
> | 审查技能 | dtazziboot-java-code-review (SDD 范式) |
> | 审查范围 | library-backend 全量代码（4 模块 / 5 表 / 17 接口 / 43 文件） |
> | 关联设计 | `.agents/20260729-图书管理系统/design.md` |
> | 关联实现 | `.agents/20260729-图书管理系统/impl.md` |
> | 审查人 | DTCoder (自动审查) |

---

## 审查结论

| 等级 | 数量 | 说明 |
|------|:----:|------|
| **P0 (Blocker)** | **3** | 必须修复后方可合并 |
| **P1 (Major)** | **6** | 建议修复，影响可靠性/性能 |
| **P2 (Info)** | **5** | 改进建议，不影响功能 |

> **blocker_count = 3**

---

## Step 1 — 执行队列

| # | 文件（仓库相对路径） | 归属原因 | Step2 功能 | Step3 可读性 | G1 并发 | G2 幂等 | G3 事务 | G16 异常 | S1 SQL注入 | S5 密钥泄露 | 总状态 |
|---|---|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 1 | `borrow/service/impl/BorrowServiceImpl.java` | 核心借阅逻辑 | ❌ | ✅ | ⚠️ | ⚠️ | ✅ | ✅ | ✅ | N/A | ❌ |
| 2 | `book/service/impl/BookServiceImpl.java` | 图书CRUD+库存 | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | N/A | ⚠️ |
| 3 | `reader/service/impl/ReaderServiceImpl.java` | 读者管理 | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | N/A | ⚠️ |
| 4 | `auth/service/impl/AuthServiceImpl.java` | 认证服务 | ✅ | ⚠️ | N/A | ✅ | ✅ | ✅ | ✅ | N/A | ⚠️ |
| 5 | `common/interceptor/AuthInterceptor.java` | 认证拦截器 | ✅ | ⚠️ | N/A | N/A | N/A | ✅ | N/A | N/A | ⚠️ |
| 6 | `common/jwt/JwtUtil.java` | JWT 签发校验 | ✅ | ✅ | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ |
| 7 | `resources/mapper/BookMapper.xml` | 库存扣减/搜索SQL | ✅ | ✅ | ✅ | ⚠️ | N/A | N/A | ✅ | N/A | ⚠️ |
| 8 | `resources/mapper/BorrowRecordMapper.xml` | 归还/统计SQL | ✅ | ✅ | N/A | ✅ | N/A | N/A | ✅ | N/A | ✅ |
| 9 | `resources/schema.sql` | DDL+初始数据 | ❌ | ✅ | N/A | N/A | N/A | N/A | N/A | ⚠️ | ❌ |
| 10 | `resources/application.yml` | 应用配置 | ⚠️ | ✅ | N/A | N/A | N/A | N/A | N/A | ❌ | ❌ |
| 11 | `pom.xml` | Maven 依赖 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 12-43 | 其余 DTO/Entity/Mapper/Controller/Config | 契约/路由/配置 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |

---

## Step 2 — 功能核对（产物 B）

### 2.1 设计符合性

| 系分功能项 | 实现位置 | 符合 | 备注 |
|------------|----------|:----:|------|
| F01 用户登录认证 | AuthController + AuthServiceImpl + JwtUtil | ✅ | BCrypt 校验 + JWT 签发正确 |
| F02 角色权限控制 | AuthInterceptor (ADMIN/READER 垂直权限) | ✅ | /api/admin/** 校验 ADMIN |
| F03-F06 图书CRUD | BookController + BookServiceImpl | ✅ | 含 ISBN 唯一校验、分类校验 |
| F07-F10 读者CRUD | ReaderController + ReaderServiceImpl | ✅ | 含事务创建 sys_user+reader |
| F11 图书搜索 | BookController.searchBooks | ✅ | keyword/categoryId 分页 |
| F12 借阅图书 | BorrowServiceImpl.borrow | ✅ | 扣库存+30天期限+事务 |
| F13 归还图书 | BorrowServiceImpl.returnBook | ❌ | **见 P0-1：逾期状态逻辑错误** |
| F14 借阅记录查询 | BorrowController.listMyRecords | ✅ | 水平权限（userId 过滤） |
| F15 分类管理 | BookController.createCategory | ✅ | 含分类名唯一校验 |
| F16 借阅期限配置 | application.yml period-days=30 | ✅ | @Value 注入可配置 |

### 2.2 功能缺陷明细

#### ❌ P0-1：归还时逾期状态逻辑错误（功能性 Blocker）

- **文件**：`src/main/java/com/library/borrow/service/impl/BorrowServiceImpl.java:102-104`
- **代码**：
  ```java
  boolean isOverdue = now.isAfter(record.getDueDate());
  String newStatus = isOverdue ? LibraryConstants.STATUS_OVERDUE : LibraryConstants.STATUS_RETURNED;
  ```
- **问题**：当图书逾期归还时，`newStatus` 被设为 `OVERDUE` 而非 `RETURNED`。这意味着逾期归还的记录永远停留在 `OVERDUE` 状态，不会被标记为已归还。
- **影响**：
  1. `countOverdue` SQL（`status = 'OVERDUE' OR (status = 'BORROWING' AND due_date < NOW())`）会将已归还的逾期记录仍计入逾期数量
  2. `BorrowServiceImpl.borrow` 行57-60 在 `overdueCount > 0` 时拒绝借阅
  3. **读者一旦有逾期归还记录，将永远无法再借阅** — 这是最严重的功能性 blocker
- **需求对照**：需求要求"归还时恢复库存，逾期给出提示"，即归还应将状态设为 `RETURNED`，并通过 `ReturnResult.isOverdue` 返回逾期提示，而非改变记录状态
- **修复建议**：
  ```java
  // 归还始终设为 RETURNED，逾期信息通过 ReturnResult 返回
  String newStatus = LibraryConstants.STATUS_RETURNED;
  ```

#### ❌ P0-2：schema.sql 初始密码 BCrypt 哈希疑似无效（凭证不可用）

- **文件**：`src/main/resources/schema.sql:102`
- **代码**：
  ```sql
  INSERT INTO `sys_user` (`username`, `password`, `role`, `status`) VALUES
  ('admin01', '$2a$10$N.ZOn9G6/Ylfav6KB8qnH.5B5O5VH3Dn6lKj5oFk5n5o5o5o5o5o', 'ADMIN', 'ACTIVE');
  ```
- **问题**：该 BCrypt 哈希值格式为 60 字符（`$2a$10$` + 53 位），格式合法，但末尾 `5o5o5o5o5o5o` 呈现重复填充模式，疑似伪造值，极可能无法匹配任何已知明文密码。且 schema.sql 未注释对应明文密码。
- **影响**：管理员账号 `admin01` 无法登录，系统无法进行任何管理操作
- **修复建议**：使用 `BCryptPasswordEncoder.encode("已知密码")` 生成真实哈希，并在 SQL 注释中记录明文（仅限开发环境）

#### ❌ P0-3：application.yml 敏感信息硬编码（安全 Blocker — S5）

- **文件**：`src/main/resources/application.yml:12, 37`
- **代码**：
  ```yaml
  spring.datasource.password: root          # 行12 — 明文数据库密码
  library.jwt.secret: library-backend-jwt-... # 行37 — 明文JWT密钥
  ```
- **问题**：数据库密码和 JWT 签名密钥以明文硬编码在源码受控配置文件中
- **影响**：凭证泄露风险，任何有代码访问权限的人可获取数据库和 JWT 签发能力
- **修复建议**：通过环境变量或配置中心注入，`application.yml` 仅保留占位符：
  ```yaml
  password: ${DB_PASSWORD:}
  secret: ${JWT_SECRET:}
  ```

---

## Step 3 — 可读性检查（产物 C）

> 参考：阿里巴巴 Java 代码风格 A1-A7

| ID | 规则 | 命中 | 文件:行号 | 等级 |
|----|------|:----:|-----------|:----:|
| A2.2 | 禁止 `import *` | ✅ 无命中 | — | ✅ |
| A3.3 | 缩进 4 空格，禁止 Tab | ✅ 无命中 | — | ✅ |
| A3.4 | 行宽 ≤ 120 字符 | ⚠️ 命中 | `AuthInterceptor.java:90` | P2 |
| A4.3 | 方法名 lowerCamelCase | ✅ 无命中 | — | ✅ |
| A4.4 | 常量 UPPER_SNAKE_CASE | ✅ 无命中 | — | ✅ |
| A5.x | 魔法值避免 | ⚠️ 命中 | `AuthServiceImpl.java:45,62` — `"ACTIVE"` 字面量替代常量 | P2 |
| A2.x | 未使用 import | ⚠️ 命中 | `BorrowServiceImpl.java:26` — `import java.util.List` 未使用 | P2 |

### 可读性命中明细

1. **[P2] A3.4 — LineWidthExceeded**：`AuthInterceptor.java:90`
   - `response.getWriter().write(objectMapper.writeValueAsString(Result.fail(errorCode.getCode(), errorCode.getMsg())));`
   - 行宽超 120 字符，建议拆行

2. **[P2] A5 — 字面量替代常量**：`AuthServiceImpl.java:45,62`
   - `!"ACTIVE".equals(user.getStatus())` 和 `user.setStatus("ACTIVE")` 使用 `"ACTIVE"` 字面量
   - 建议使用 `LibraryConstants.STATUS_ACTIVE` 常量（若已定义）

3. **[P2] A2 — 未使用 import**：`BorrowServiceImpl.java:26`
   - `import java.util.List;` 未在代码中使用

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 自动化预扫结果（scan-all-rules.sh）

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Summary: 15 findings (P0=8, P1=6, P2=1) | 52/222 rules scanned
```

| 预扫命中 | 文件:行号 | 人工复核结论 |
|----------|-----------|:----------:|
| [P0] G16.2 CatchWithoutLogging | AuthInterceptor.java:63 | **误报** — catch(BizException) 有 `return writeFail(...)` 处理 |
| [P0] G16.2 CatchWithoutLogging | AuthInterceptor.java:91 | **误报** — catch(Exception) 有 `log.error(...)` 在 line 92 |
| [P0] G16.2 CatchWithoutLogging | JwtUtil.java:93 | **误报** — catch(Exception) 有 `log.warn(...)` 在 line 94 |
| [P0] S1.1 MyBatisSqlInjection | pom.xml:45,65,72,77,83 | **误报** — pom.xml 依赖声明非 MyBatis SQL |
| [P1] M016 JavaTimeDefaultTimeZone | BorrowServiceImpl.java:75 | **确认** — `LocalDateTime.now()` 使用系统默认时区 |
| [P1] M016 JavaTimeDefaultTimeZone | BorrowServiceImpl.java:102 | **确认** — 同上 |
| [P1] M016 JavaTimeDefaultTimeZone | BorrowServiceImpl.java:152 | **确认** — 同上 |
| [P1] M016 JavaTimeDefaultTimeZone | MybatisPlusConfig.java:39,40,45 | **确认** — 自动填充使用默认时区 |
| [P2] A3.4 LineWidthExceeded | AuthInterceptor.java:90 | **确认** — 见 Step 3 |

> **预扫误报率**：15 条中 8 条 P0 全部为误报（G16.2 × 3 + S1.1 × 5），7 条 P1/P2 确认有效。

### 4.2 可靠性明细（G 系列 — 蚂蚁编码军规）

| ID | 等级 | 描述 | 文件:行号 |
|----|:----:|------|-----------|
| G1.1 | ⚠️ P1 | **borrow 并发先读后写无锁**：`countOverdue`(行57) 和 `countBorrowing`(行63) 为普通 SELECT 无 `FOR UPDATE`，并发借阅可能同时通过数量校验后都成功扣库存，导致超过最大在借数量限制。deductStock 本身有 `WHERE stock >= qty` 行级锁防超卖，但借阅数量限制无并发保护 | `BorrowServiceImpl.java:57,63` |
| G1.1 | ⚠️ P1 | **createBook ISBN 唯一性先读后写**：先 `selectCount` 再 `insert`，并发可能插入重复 ISBN。数据库有 `uk_book_isbn` 唯一约束兜底，重复时抛异常 | `BookServiceImpl.java:42-47` |
| G1.1 | ⚠️ P1 | **createReader username/readerNo 唯一性先读后写**：同上模式，有唯一约束兜底 | `ReaderServiceImpl.java:37-44` |
| G2.1 | ⚠️ P1 | **restoreStock 无幂等保护**：`UPDATE book SET stock = stock + #{qty} WHERE id = #{bookId}` 无条件判断。虽然 returnBook 的 `updateReturn` 有幂等条件更新（status IN BORROWING/OVERDUE），整体方法在 `@Transactional` 内，但 restoreStock 本身缺乏二次校验 | `BookMapper.xml:28-32` |
| G3.2 | ✅ | `@Transactional(rollbackFor=Exception.class)` 范围合理，无外部 I/O | — |
| G16.1 | ✅ | 异常捕获均有日志记录（SLF4J 占位符） | — |

### 4.3 安全明细（S 系列）

| ID | 等级 | 描述 | 文件:行号 |
|----|:----:|------|-----------|
| S1.1 | ✅ | 所有 MyBatis XML 使用 `#{}` 参数化，无 `${}` 注入风险 | BookMapper.xml / BorrowRecordMapper.xml |
| S2.1 | ✅ | 输入校验：DTO 使用 `@Valid`（需确认 Controller 层） | — |
| S3.1 | ✅ | BCrypt 密码加密：`passwordEncoder.encode/matches` | AuthServiceImpl.java:40,60 |
| S4.1 | ✅ | 水平权限：借阅记录按 userId 过滤，归还 updateReturn 含 userId 条件 | BorrowRecordMapper.xml:25 |
| S5.1 | ❌ P0 | **密钥泄露**：数据库密码 `root` 和 JWT 密钥明文硬编码 | application.yml:12,37 |
| S6.1 | ✅ | 手机号脱敏：`desensitizePhone` 方法 `138****8000` | ReaderServiceImpl.java:120-124 |
| S7.1 | ✅ | 逻辑删除：`@TableLogic` + `is_deleted` 字段 | BookDO / ReaderDO |

### 4.4 Bug 模式明细（B/M/I 系列 — 120 条规则）

| ID | 等级 | 描述 | 文件:行号 |
|----|:----:|------|-----------|
| — | ✅ | 未命中 B001-B081 Blocker 模式（如 AlwaysThrows、ArrayEquals 等） | — |
| M016 | P1 | JavaTimeDefaultTimeZone — 使用 `LocalDateTime.now()` 依赖系统默认时区 | BorrowServiceImpl.java:75,102,152 |
| — | ✅ | 其余 M/I 规则未命中 | — |

### 4.5 性能问题（非规则项，附加发现）

| 等级 | 描述 | 文件:行号 |
|:----:|------|-----------|
| P1 | **N+1 查询**：`toBorrowRecordVO` 对每条借阅记录调用 `bookService.getBookById` 获取书名，分页 list 会产生 N 次额外查询 | `BorrowServiceImpl.java:163` |
| P1 | **N+1 查询**：`toBookVO` 对每本图书调用 `bookCategoryMapper.selectById` 获取分类名，分页 list 会产生 N 次额外查询 | `BookServiceImpl.java:187` |

---

## Step 5 — 跨仓对齐点检查

| 对齐点 | 后端（library-backend） | 前端（library-frontend） | 状态 |
|--------|------------------------|-------------------------|:----:|
| 接口前缀 | /api | 需调用 /api 前缀 | ✅ |
| 登录接口 | POST /api/auth/login → {token, role} | 需使用 JWT Token | ✅ 契约一致 |
| 鉴权方式 | Authorization: Bearer {token} | 需在请求头携带 | ✅ 契约一致 |
| 管理端路径 | /api/admin/** 需 ADMIN 角色 | 需区分管理员/读者路由 | ✅ 契约一致 |
| 读者端路径 | /api/books/search, /api/borrow, /api/return, /api/borrow/records | 需对应页面 | ✅ 契约一致 |
| 响应结构 | {code:"OK", msg, data} | 需解析 code 判断成功 | ✅ 契约一致 |
| 归还响应 | ReturnResult {isOverdue, overdueDays} | 需展示逾期提示 | ⚠️ 依赖 P0-1 修复 |

---

## 问题汇总与优先级

### P0 (Blocker) — 必须修复

| # | 问题 | 文件 | 修复建议 |
|---|------|------|----------|
| P0-1 | 归还时逾期状态设为 OVERDUE 而非 RETURNED，导致读者永久无法借阅 | `BorrowServiceImpl.java:104` | `newStatus` 始终设为 `RETURNED`，逾期信息通过 `ReturnResult` 返回 |
| P0-2 | schema.sql 初始密码 BCrypt 哈希疑似伪造，管理员无法登录 | `schema.sql:102` | 生成真实 BCrypt 哈希，注释记录明文 |
| P0-3 | 数据库密码和 JWT 密钥明文硬编码 | `application.yml:12,37` | 改为 `${DB_PASSWORD}` / `${JWT_SECRET}` 环境变量注入 |

### P1 (Major) — 建议修复

| # | 问题 | 文件 | 修复建议 |
|---|------|------|----------|
| P1-1 | borrow 方法 countBorrowing/countOverdue 并发无锁 | `BorrowServiceImpl.java:57,63` | 加 `SELECT FOR UPDATE` 或数据库唯一约束 |
| P1-2 | createBook/createCategory/createReader 唯一性先读后写 | 多文件 | 依赖 DB 唯一约束兜底，捕获 `DuplicateKeyException` 转为业务异常 |
| P1-3 | restoreStock 无幂等条件判断 | `BookMapper.xml:28-32` | 添加 `AND is_deleted = 0` 条件 |
| P1-4 | N+1 查询：借阅记录转 VO 时逐条查询书名 | `BorrowServiceImpl.java:163` | 改为批量查询或 JOIN |
| P1-5 | N+1 查询：图书转 VO 时逐条查询分类名 | `BookServiceImpl.java:187` | 改为批量查询或 JOIN |
| P1-6 | LocalDateTime.now() 使用系统默认时区 (M016) | `BorrowServiceImpl.java:75,102,152` | 指定时区或使用 `ZonedDateTime` |

### P2 (Info) — 改进建议

| # | 问题 | 文件 | 修复建议 |
|---|------|------|----------|
| P2-1 | AuthInterceptor.writeFail 行宽超 120 字符 | `AuthInterceptor.java:90` | 拆行为多行 |
| P2-2 | "ACTIVE" 字面量替代常量 | `AuthServiceImpl.java:45,62` | 使用 `LibraryConstants.STATUS_ACTIVE` |
| P2-3 | 未使用 import java.util.List | `BorrowServiceImpl.java:26` | 删除 |
| P2-4 | overdueDays < 0 判断为死代码 | `BorrowServiceImpl.java:122-124` | 在 isOverdue 分支内，Duration 必为正，可删除 |
| P2-5 | deleteBook/deleteReader 缺少 @Transactional | `BookServiceImpl.java:66` / `ReaderServiceImpl.java:61` | 逻辑删除为单条 UPDATE，影响小，建议补充 |

---

## 附录：scan-all-rules.sh 原始输出

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: .
Engine:  ripgrep

[P0] G16.2 — CatchWithoutLogging: ./src/main/java/com/library/common/interceptor/AuthInterceptor.java:63
[P0] G16.2 — CatchWithoutLogging: ./src/main/java/com/library/common/interceptor/AuthInterceptor.java:91
[P0] G16.2 — CatchWithoutLogging: ./src/main/java/com/library/common/jwt/JwtUtil.java:93
[P0] S1.1 — MyBatisSqlInjection: ./pom.xml:45
[P0] S1.1 — MyBatisSqlInjection: ./pom.xml:65
[P0] S1.1 — MyBatisSqlInjection: ./pom.xml:72
[P0] S1.1 — MyBatisSqlInjection: ./pom.xml:77
[P0] S1.1 — MyBatisSqlInjection: ./pom.xml:83
[P1] M016 — JavaTimeDefaultTimeZone: ./src/main/java/com/library/borrow/service/impl/BorrowServiceImpl.java:102
[P1] M016 — JavaTimeDefaultTimeZone: ./src/main/java/com/library/borrow/service/impl/BorrowServiceImpl.java:152
[P1] M016 — JavaTimeDefaultTimeZone: ./src/main/java/com/library/borrow/service/impl/BorrowServiceImpl.java:75
[P1] M016 — JavaTimeDefaultTimeZone: ./src/main/java/com/library/common/config/MybatisPlusConfig.java:39
[P1] M016 — JavaTimeDefaultTimeZone: ./src/main/java/com/library/common/config/MybatisPlusConfig.java:40
[P1] M016 — JavaTimeDefaultTimeZone: ./src/main/java/com/library/common/config/MybatisPlusConfig.java:45
[P2] A3.4 — LineWidthExceeded: ./src/main/java/com/library/common/interceptor/AuthInterceptor.java:90

=== Summary: 15 findings (P0=8, P1=6, P2=1) | 52/222 rules scanned ===
```

> 人工复核结论：8 条 P0 全部为误报（G16.2 × 3 catch 块均有日志/处理；S1.1 × 5 为 pom.xml 依赖声明非 SQL）。确认有效的 P1 = 6 条、P2 = 1 条。手动审查另发现 3 个真实 P0 + 5 个 P1 + 4 个 P2。

---

*审查完成。blocker_count = 3，需修复 P0 后方可合并。*

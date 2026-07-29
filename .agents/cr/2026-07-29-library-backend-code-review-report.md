# Code Review Report

> **Change** `编码实现 (stage: coding)` · **分支/Commit** `AI/task-DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-f4e7cfb4-b699-4f60-` / `06a7123` · **日期** `2026-07-29` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 34 |
| 变更行数 | `+2617 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|--------------|
| LibraryApplication | `src/main/java/com/library/LibraryApplication.java` | 启动类 |
| BookController | `src/main/java/com/library/controller/BookController.java` | 图书接口 |
| BorrowController | `src/main/java/com/library/controller/BorrowController.java` | 借阅接口 |
| ReaderController | `src/main/java/com/library/controller/ReaderController.java` | 读者接口 |
| StatisticsController | `src/main/java/com/library/controller/StatisticsController.java` | 统计接口 |
| BookServiceImpl | `src/main/java/com/library/service/impl/BookServiceImpl.java` | 图书实现 |
| BorrowServiceImpl | `src/main/java/com/library/service/impl/BorrowServiceImpl.java` | 借阅实现 |
| ReaderServiceImpl | `src/main/java/com/library/service/impl/ReaderServiceImpl.java` | 读者实现 |
| StatisticsServiceImpl | `src/main/java/com/library/service/impl/StatisticsServiceImpl.java` | 统计实现 |
| GlobalExceptionHandler | `src/main/java/com/library/common/GlobalExceptionHandler.java` | 全局异常 |
| BusinessConstants | `src/main/java/com/library/common/BusinessConstants.java` | 状态常量 |
| BookMapper.xml | `src/main/resources/mapper/BookMapper.xml` | 图书SQL |
| BorrowRecordMapper.xml | `src/main/resources/mapper/BorrowRecordMapper.xml` | 借阅SQL |
| schema.sql | `src/main/resources/schema.sql` | 建表DDL |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 2 | 4 |

---

## 3. Step 2 — 功能（REQ）

### REQ-5: 逾期提示

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 逾期归还给出提示 | ⚠️ | "逾期给出提示" | `BorrowServiceImpl.java:111-113`、`BorrowController.java:39-41` | Controller 层有逾期提示拼接，但底层状态语义缺陷（见 P0-2），导致已逾期归还的记录被误判为"未归还逾期" |

> 其余 REQ-1~4、REQ-6~11 均 ✅ 通过（详见 checklist Step 2）。

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ❌ | A2.2 通配符 import：`BookController.java:9`、`BorrowController.java:9`、`ReaderController.java:9`（`import org.springframework.web.bind.annotation.*`） |
| ⚠️ | A5 冗余字段：`StatisticsServiceImpl.java:133` `private static final java.util.Date NOW_DUMMY = null;`（第19行已 import，此字段无业务用途） |
| ⚠️ | A7 部分 Service 方法缺 Javadoc（`BorrowServiceImpl.borrowBook` 等） |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ❌ | P0 | G1 并发控制：`BorrowServiceImpl.java:66` deductStock 未检查返回值；G1 重复借阅 TOCTOU：`:60`→`:66` |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P1 | S2 认证授权：`application.yml` 无安全配置，管理员/读者接口均无角色鉴权 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I | ❌ | P2 | I004 JavaUtilDate：`BorrowServiceImpl.java:72,109,158,180`（预扫命中） |
| schema | `schema.sql` | ⚠️ | P1 | borrow_record 无 `(reader_id,book_id,status)` 唯一索引兜底 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | N/A | N/A(未启用自定义规则) |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. `BorrowServiceImpl.java:111-113` 逾期归还状态语义缺陷 —— 逾期归还后 status 设为 `OVERDUE` 而非 `RETURNED`，导致 `convertToVO:179` 的 `!BORROW_STATUS_RETURNED.equals(status)` 判断将"已逾期归还"记录误判为"未归还仍在逾期"，逾期天数持续累加，数据语义错误
- **P1**：
  1. `BorrowServiceImpl.java:66` 借阅扣减库存后未检查返回值（对比 `BookServiceImpl.java:123-126` 检查了 rows），并发下 DB 守卫使一个 UPDATE 返回 0 行但代码仍 insert 借阅记录，产生"有借阅记录但库存未扣"的数据不一致
  2. `application.yml` / Controller 层无角色鉴权，需求明确"管理员/读者两种角色"但管理员接口（增删改查）与读者接口均无权限校验，任意调用方可执行 `DELETE /api/books/{id}`
- **P2**：
  1. A2.2 通配符 import（`BookController:9`/`BorrowController:9`/`ReaderController:9`）
  2. I004 使用 `java.util.Date`（`BorrowServiceImpl:72,109,158,180`），建议 `java.time.LocalDateTime`
  3. A5 冗余字段 `StatisticsServiceImpl:133` `NOW_DUMMY`
  4. `BorrowRequest.borrowDays` 无上界校验
- **一句话**：核心业务流程完整、SQL 参数化规范、事务覆盖到位，但借阅归还的状态机语义缺陷与库存扣减返回值未检查是必须修复的阻塞项，鉴权缺失需在上线前补齐。

---

## 7.1 问题片段（必填）

### P0-1 借阅归还状态语义缺陷

- **P0** `BorrowServiceImpl.java:111-113` — 逾期归还时 status 设为 `OVERDUE` 而非 `RETURNED`，导致 `convertToVO` 将已逾期归还记录误判为"未归还仍在逾期"。
  片段范围：`src/main/java/com/library/service/impl/BorrowServiceImpl.java:105-131`

```java
L105|        // 3. 恢复库存
L106|        bookMapper.restoreStock(record.getBookId(), 1);
L107|
L108|        // 4. 判定是否逾期
L109|        Date now = new Date();
L110|        boolean overdue = now.after(record.getDueTime());
L111|        String status = overdue
L112|                ? BusinessConstants.BORROW_STATUS_OVERDUE      // 问题：逾期归还应仍为 RETURNED，用 returnTime/overdue 字段表达逾期，而非把状态留为 OVERDUE
L113|                : BusinessConstants.BORROW_STATUS_RETURNED;
L114|
L115|        // 5. 更新归还记录
L116|        borrowRecordMapper.updateReturn(recordId, now, status);
...
L179|        if (!BusinessConstants.BORROW_STATUS_RETURNED.equals(record.getStatus())) {  // 问题：OVERDUE 状态的已归还记录会进入此分支，被误判为"未归还仍在逾期"
L180|            Date now = new Date();
L181|            boolean overdue = now.after(record.getDueTime());
L182|            vo.setOverdue(overdue);
L183|            vo.setOverdueDays(overdue ? calculateOverdueDays(record.getDueTime(), now) : 0);
```

> 修复方向：逾期归还后 status 应设为 `RETURNED`（return_time 非空即表示已归还），用 `BorrowVO.overdue`/`overdueDays` 字段表达逾期信息；`convertToVO` 判断已归还应以 `return_time != null` 为准，而非 status==RETURNED。

### P1-1 借阅扣减库存未检查返回值

- **P1** `BorrowServiceImpl.java:66` — 调用 `bookMapper.deductStock` 后未检查返回值，并发下 DB 守卫使一个 UPDATE 返回 0 行但代码仍继续 insert 借阅记录。
  片段范围：`src/main/java/com/library/service/impl/BorrowServiceImpl.java:59-84`

```java
L59|        // 3. 校验是否重复借阅同一本未归还图书
L60|        int activeBorrowCount = borrowRecordMapper.countActiveBorrow(request.getReaderId(), request.getBookId());
L61|        if (activeBorrowCount > 0) {
L62|            throw new BusinessException(ResultCode.DUPLICATE_BORROW, "您已借阅该图书且尚未归还");
L63|        }
L64|
L65|        // 4. 扣减库存
L66|        bookMapper.deductStock(request.getBookId(), 1);   // 问题：未检查返回值，对比 BookServiceImpl.java:123-126 检查了 rows<=0
L67|
L68|        // 5. 创建借阅记录, 期限默认30天
...
L84|        borrowRecordMapper.insert(record);   // 问题：即使库存扣减 rows=0，仍会 insert 借阅记录
```

> 修复方向：第66行改为 `int rows = bookMapper.deductStock(request.getBookId(), 1); if (rows <= 0) { throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH, "库存不足"); }`

### P1-2 管理员/读者接口无角色鉴权

- **P1** `application.yml` / `BookController.java:58-79` — 需求明确"管理员/读者两种角色"，但管理员接口（图书增删改、读者增删改）与读者接口均无权限校验。
  片段范围：`src/main/java/com/library/controller/BookController.java:55-79`

```java
L55|    /**
L56|     * 新增图书
L57|     */
L58|    @PostMapping
L59|    public Result<Long> addBook(@Valid @RequestBody BookRequest request) {   // 问题：无 @PreAuthorize / 角色校验
L60|        return Result.success(bookService.addBook(request));
L61|    }
...
L72|    /**
L73|     * 删除图书
L74|     */
L75|    @DeleteMapping("/{id}")
L76|    public Result<Void> deleteBook(@PathVariable Long id) {   // 问题：任意调用方可删除图书
L77|        bookService.deleteBook(id);
L78|        return Result.success();
L79|    }
```

> 修复方向：引入 Spring Security 或自定义拦截器/注解，对管理员接口加角色校验（如 `@PreAuthorize("hasRole('ADMIN')")`），application.yml 补充安全配置。

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/java/com/library/service/impl/BorrowServiceImpl.java:111-113` — 逾期归还后 status 改设为 `RETURNED`，用 return_time 非空 + overdue 字段表达逾期；同步修改 `convertToVO:179` 以 `return_time != null` 判断已归还

### P1

- [ ] **P1** `src/main/java/com/library/service/impl/BorrowServiceImpl.java:66` — 检查 `bookMapper.deductStock` 返回值，rows<=0 时抛 `STOCK_NOT_ENOUGH` 异常
- [ ] **P1** `src/main/resources/schema.sql` — 为 `borrow_record` 添加 `(reader_id, book_id, status)` 唯一索引（仅 BORROWED/OVERDUE 状态）兜底重复借阅
- [ ] **P1** `src/main/resources/application.yml` + Controller — 引入角色鉴权，管理员接口（图书/读者增删改）加 `@PreAuthorize("hasRole('ADMIN')")` 或拦截器校验

### P2（可选）

- [ ] **P2** `src/main/java/com/library/controller/BookController.java:9` — 消除通配符 import，改为显式 import（BorrowController:9、ReaderController:9 同改）
- [ ] **P2** `src/main/java/com/library/service/impl/BorrowServiceImpl.java:72,109,158,180` — `java.util.Date` 改为 `java.time.LocalDateTime`（I004）
- [ ] **P2** `src/main/java/com/library/service/impl/StatisticsServiceImpl.java:133` — 删除冗余字段 `NOW_DUMMY`
- [ ] **P2** `src/main/java/com/library/model/dto/BorrowRequest.java:27` — 为 `borrowDays` 加 `@Max` 上界校验

# Code Review Report

> **Change** `实现简单图书管理系统` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e4c30e24-0b27-44a9-bfb0-` / `HEAD` · **日期** `2026-07-31` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID：可读性 `A3.4`，安全 `S1.1`，可靠性 `G16.2`，Bug 模式 `B012` / `M005` 等。**每个 ❌/⚠️ 问题在 §7 后必须附 `.java` 问题片段**（见 §7.1）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `36` |
| 变更行数 | `+1306 / -0`（全量新增） |

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| `LibraryApplication` | `src/main/java/com/antgroup/library/LibraryApplication.java` | 启动类 |
| `BookController` | `src/main/java/com/antgroup/library/book/controller/BookController.java` | 图书 Controller（W01-W05） |
| `BookCreateRequest` | `src/main/java/com/antgroup/library/book/dto/BookCreateRequest.java` | 新增图书请求 DTO |
| `BookQueryRequest` | `src/main/java/com/antgroup/library/book/dto/BookQueryRequest.java` | 图书分页查询请求 DTO |
| `BookUpdateRequest` | `src/main/java/com/antgroup/library/book/dto/BookUpdateRequest.java` | 修改图书请求 DTO |
| `BookVO` | `src/main/java/com/antgroup/library/book/dto/BookVO.java` | 图书返回视图 |
| `Book` | `src/main/java/com/antgroup/library/book/entity/Book.java` | 图书实体 |
| `BookDeleteFlag` | `src/main/java/com/antgroup/library/book/enums/BookDeleteFlag.java` | 图书逻辑删除标记 |
| `BookMapper` | `src/main/java/com/antgroup/library/book/mapper/BookMapper.java` | 图书 Mapper 接口 |
| `BookService` | `src/main/java/com/antgroup/library/book/service/BookService.java` | 图书服务接口 |
| `BookServiceImpl` | `src/main/java/com/antgroup/library/book/service/impl/BookServiceImpl.java` | 图书服务实现 |
| `BorrowRecordController` | `src/main/java/com/antgroup/library/borrow/controller/BorrowRecordController.java` | 借阅 Controller（W11-W13） |
| `BorrowRecordQueryRequest` | `src/main/java/com/antgroup/library/borrow/dto/BorrowRecordQueryRequest.java` | 借阅记录分页查询请求 DTO |
| `BorrowRecordVO` | `src/main/java/com/antgroup/library/borrow/dto/BorrowRecordVO.java` | 借阅记录返回视图 |
| `BorrowRequest` | `src/main/java/com/antgroup/library/borrow/dto/BorrowRequest.java` | 借书请求 DTO |
| `BorrowRecord` | `src/main/java/com/antgroup/library/borrow/entity/BorrowRecord.java` | 借阅记录实体 |
| `BorrowStatus` | `src/main/java/com/antgroup/library/borrow/enums/BorrowStatus.java` | 借阅状态枚举 |
| `BorrowRecordMapper` | `src/main/java/com/antgroup/library/borrow/mapper/BorrowRecordMapper.java` | 借阅 Mapper 接口 |
| `BorrowRecordService` | `src/main/java/com/antgroup/library/borrow/service/BorrowRecordService.java` | 借阅服务接口 |
| `BorrowRecordServiceImpl` | `src/main/java/com/antgroup/library/borrow/service/impl/BorrowRecordServiceImpl.java` | 借阅服务实现 |
| `BizException` | `src/main/java/com/antgroup/library/common/exception/BizException.java` | 业务异常 |
| `ErrorCode` | `src/main/java/com/antgroup/library/common/exception/ErrorCode.java` | 错误码枚举 |
| `GlobalExceptionHandler` | `src/main/java/com/antgroup/library/common/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| `PageQuery` | `src/main/java/com/antgroup/library/common/response/PageQuery.java` | 分页基类 |
| `PageResult` | `src/main/java/com/antgroup/library/common/response/PageResult.java` | 分页返回结构 |
| `Result` | `src/main/java/com/antgroup/library/common/response/Result.java` | 通用返回结构 |
| `ReaderController` | `src/main/java/com/antgroup/library/reader/controller/ReaderController.java` | 读者 Controller（W06-W10） |
| `ReaderCreateRequest` | `src/main/java/com/antgroup/library/reader/dto/ReaderCreateRequest.java` | 新增读者请求 DTO |
| `ReaderQueryRequest` | `src/main/java/com/antgroup/library/reader/dto/ReaderQueryRequest.java` | 读者分页查询请求 DTO |
| `ReaderUpdateRequest` | `src/main/java/com/antgroup/library/reader/dto/ReaderUpdateRequest.java` | 修改读者请求 DTO |
| `ReaderVO` | `src/main/java/com/antgroup/library/reader/dto/ReaderVO.java` | 读者返回视图 |
| `Reader` | `src/main/java/com/antgroup/library/reader/entity/Reader.java` | 读者实体 |
| `ReaderStatus` | `src/main/java/com/antgroup/library/reader/enums/ReaderStatus.java` | 读者状态枚举 |
| `ReaderMapper` | `src/main/java/com/antgroup/library/reader/mapper/ReaderMapper.java` | 读者 Mapper 接口 |
| `ReaderService` | `src/main/java/com/antgroup/library/reader/service/ReaderService.java` | 读者服务接口 |
| `ReaderServiceImpl` | `src/main/java/com/antgroup/library/reader/service/impl/ReaderServiceImpl.java` | 读者服务实现 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 3 | 6 | 5 |

---

## 3. Step 2 — 功能（REQ）

> 基于设计文档 F01-F11 功能点、W01-W13 接口定义、R01-R20 业务规则进行 Spec 绑定核对。

### REQ-F01: 图书新增（W03）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 新增图书——ISBN 唯一校验 | ✅ | `design.md R03: ISBN唯一约束` | `BookServiceImpl.java:66-69` | createBook 先 selectByIsbn 再 insert，实现唯一校验 |
| 新增图书——totalStock=stock | ✅ | `design.md 5.1.1.2: total_stock=初始库存` | `BookServiceImpl.java:77` | `book.setTotalStock(request.getStock())` 符合规约 |
| 新增图书——参数校验 | ✅ | `design.md W03 入参约束` | `BookCreateRequest.java:16-38` | `@NotBlank`/`@Size`/`@NotNull`/`@PositiveOrZero` 完整 |
| 新增图书——Controller `@Valid` | ✅ | `design.md W03` | `BookController.java:43` | `@Valid @RequestBody` 正确 |

### REQ-F02: 图书分页查询（W01）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 分页查询——title/author LIKE | ✅ | `design.md W01 模糊查询` | `BookMapper.xml:41-49` | LIKE CONCAT('%', #{title}, '%') 正确使用 #{} 参数化 |
| 分页查询——count+page 两步 | ✅ | `design.md W01 分页` | `BookServiceImpl.java:40-51` | 先 selectCount 再 selectPage，total=0 提前返回 |
| 分页查询——分页参数规范化 | ✅ | `design.md 分页默认值约束` | `PageQuery.java:22-34` | normalizedPageNum/normalizedPageSize 正确实现 |

### REQ-F03: 图书详情查询（W02）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 图书详情——不存在抛异常 | ✅ | `design.md R01: 图书不存在` | `BookServiceImpl.java:55-59` | `BOOK_001` 正确 |
| 图书详情——逻辑删除过滤 | ✅ | `design.md 5.1.1.2` | `BookMapper.xml:28` | `WHERE id=#{id} AND is_deleted=0` |

### REQ-F04: 图书修改（W04）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 修改图书——ISBN 变更时校验未归还 | ✅ | `design.md R04: 有未归还借阅记录禁止修改ISBN` | `BookServiceImpl.java:90-98` | 先 countUnreturnedByBook，再 selectByIsbn 去重 |
| 修改图书——动态 SET | ✅ | `design.md W04 局部更新` | `BookMapper.xml:74-86` | `<set>` + `<if>` 动态更新正确 |
| 修改图书——stock/totalStock 不可改 | ✅ | `design.md W04 注释` | `BookUpdateRequest.java:8` | DTO 无 stock/totalStock 字段 |

### REQ-F05: 图书删除（W05）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 逻辑删除——有未归还借阅禁止删 | ✅ | `design.md R05` | `BookServiceImpl.java:121-124` | `BOOK_004` 正确 |
| 逻辑删除——is_deleted=1 | ✅ | `design.md 5.1.1.2` | `BookMapper.xml:88-91` | `SET is_deleted=1` 正确 |

### REQ-F06: 读者新增（W08）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 新增读者——手机号唯一校验 | ✅ | `design.md R08` | `ReaderServiceImpl.java:66-69` | selectByPhone 唯一校验 |
| 新增读者——默认状态 ACTIVE | ✅ | `design.md 5.2.1.2` | `ReaderServiceImpl.java:74` | `ReaderStatus.ACTIVE.getValue()` 正确 |

### REQ-F07: 读者分页查询（W06）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 分页查询——name/phone LIKE | ✅ | `design.md W06` | `ReaderMapper.xml:32-44` | 正确参数化模糊查询 |
| 手机号脱敏 | ✅ | `design.md 6.4.3.2 手机号脱敏` | `ReaderServiceImpl.java:139,148-153` | maskPhone 中间4位替换**** |

### REQ-F08: 读者修改（W09）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 修改读者——状态合法性校验 | ✅ | `design.md R09` | `ReaderServiceImpl.java:92-94` | `ReaderStatus.isValid` 校验 |

### REQ-F09: 读者删除（W10）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 逻辑删除——有未归还借阅禁止删 | ✅ | `design.md R10` | `ReaderServiceImpl.java:114-117` | `READER_004` 正确 |

### REQ-F10: 借书（W12）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 借书——库存校验 | ✅ | `design.md R11` | `BorrowRecordServiceImpl.java:65-68` | `book.getStock() <= 0` 抛 BOOK_005 |
| 借书——读者状态校验 | ✅ | `design.md R12` | `BorrowRecordServiceImpl.java:70-73` | 非 ACTIVE 抛 READER_005 |
| 借书——借阅上限校验 | ✅ | `design.md R13: 上限5` | `BorrowRecordServiceImpl.java:75-78` | `BORROW_LIMIT=5`, `>=` 判断正确 |
| 借书——库存扣减 | ✅ | `design.md R14` | `BorrowRecordServiceImpl.java:80` | 调用 `bookService.deductStock` |
| 借书——应还时间=借阅+30天 | ✅ | `design.md R15: 30天` | `BorrowRecordServiceImpl.java:83` | `BORROW_PERIOD_DAYS=30` |
| 借书——幂等性 | ⚠️ | `design.md R16` | `BorrowRecordServiceImpl.java:64-97` | 无幂等键（见 §5 G2.1） |

### REQ-F11: 还书（W13）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 还书——状态校验 | ✅ | `design.md R17` | `BorrowRecordServiceImpl.java:107-109` | `BorrowStatus.canReturn` 校验 |
| 还书——防重还书 | ✅ | `design.md R18` | `BorrowRecordMapper.xml:98-107` | `WHERE status IN ('BORROWING','OVERDUE')` 条件更新 |
| 还书——库存恢复 | ✅ | `design.md R19` | `BorrowRecordServiceImpl.java:119` | `bookService.restoreStock` |
| 还书——逾期标记 | ✅ | `design.md R20` | `BorrowRecordServiceImpl.java:112` | `now.after(record.getDueTime())` 判断 |

---

## 4. Step 3 — 可读性检查

> 参考 `readability-checklist.md`（阿里巴巴Java代码风格指南）

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | A2.3 `ReaderServiceImpl.java:17` — import `BeanUtils` 未使用，遗留无用导入（import org.springframework.beans.BeanUtils; 实际 convertToVO 手动赋值未使用 BeanUtils） |
| ✅ | A1.x 命名规范——类名/方法名/变量名均符合驼峰命名，包名全小写 |
| ✅ | A3.x 方法长度——所有方法行数在合理范围（<50行），逻辑清晰 |
| ✅ | A4.x 注释规范——关键方法均有 Javadoc，DTO 注释关联设计文档章节编号 |
| ✅ | A5.x 常量命名——`BORROW_LIMIT`、`BORROW_PERIOD_DAYS`、`DEFAULT_PAGE_NUM` 等全大写下划线 |
| ✅ | A6.x 花括号——if/else/for 块均有花括号 |

---

## 5. Step 4 — 可靠性检查

> **scan-all-rules.sh 预扫结果（52/222 rules scanned）**：
> ```
> [P2] I004 — JavaUtilDate: BorrowRecordServiceImpl.java:111
> [P2] I004 — JavaUtilDate: BorrowRecordServiceImpl.java:82
> === Summary: 2 findings (P0=0, P1=0, P2=2) | 52/222 rules scanned ===
> ```

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性-G1 并发控制 | `reliability-checklist.md` G1 | ⚠️ | P0 | **G1.1** `BookServiceImpl.java:64-80` createBook: 先 selectByIsbn 后 insert 无锁/无唯一约束兜底，并发下可插入重复 ISBN（schema 有 uk_book_isbn 唯一约束，但 service 层无异常捕获，会抛 DataAccessException 而非 BizException） |
| 可靠性-G1 并发控制 | `reliability-checklist.md` G1 | ⚠️ | P0 | **G1.1** `BorrowRecordServiceImpl.java:64-97` borrowBook: 先读后写（selectBook→check stock→deductStock→insert record）无 `SELECT FOR UPDATE`，两个线程同时借同一本库存=1的书，都可能通过 stock>0 校验 |
| 可靠性-G1 并发控制 | `reliability-checklist.md` G1 | ⚠️ | P0 | **G1.1** `BorrowRecordServiceImpl.java:75-78` borrowBook: countUnreturnedByReader 读后无锁，并发借书可突破借阅上限=5 的约束 |
| 可靠性-G2 幂等拦截 | `reliability-checklist.md` G2 | ⚠️ | P0 | **G2.1** `BorrowRecordServiceImpl.java:64` borrowBook: 无幂等键，用户快速重复点击可创建多条借阅记录 |
| 可靠性-G2 幂等拦截 | `reliability-checklist.md` G2 | ⚠️ | P1 | **G2.2** `ReaderServiceImpl.java:66-69` createReader: 并发下 selectByPhone→insert 有 TOCTOU 竞态（schema 有 uk_reader_phone 兜底，但异常未捕获转换） |
| 可靠性-G3 事务控制 | `reliability-checklist.md` G3 | ✅ | — | **G3.2** 已扫无命中：`@Transactional(rollbackFor=Exception.class)` 范围合理，无外部 I/O |
| 可靠性-G4 空指针防御 | `reliability-checklist.md` G4 | ✅ | — | 关键字段 null 校验已覆盖（如 stock、phone、status） |
| 可靠性-G5 异常处理 | `reliability-checklist.md` G5 | ✅ | — | 全局异常处理器覆盖 BizException/validation/unknown，错误码体系完整 |
| 可靠性-G6 资源泄漏 | `reliability-checklist.md` G6 | ✅ | — | 无文件流/连接池泄漏风险（Spring 管理资源） |
| 可靠性-G7 集合处理 | `reliability-checklist.md` G7 | ✅ | — | 无集合空指针、并发修改风险 |
| 可靠性-G8 数值边界 | `reliability-checklist.md` G8 | ✅ | — | `@PositiveOrZero` 库存校验、`deductStock` SQL `stock >= #{quantity}` 边界正确 |
| 可靠性-G16 日志规范 | `reliability-checklist.md` G16 | ✅ | — | 使用 `@Slf4j` + `log.info/warn/error`，参数化占位符 `{}` |
| Bug 模式-I004 | `bug-pattern-checklist.md` I004 | ⚠️ | P2 | `BorrowRecordServiceImpl.java:82,111` — 使用 `java.util.Date`，应优先使用 `java.time` API |
| Bug 模式-B006 | `bug-pattern-checklist.md` B006 | ✅ | — | 已扫无命中：无 `equals` 比较基本类型数组 |
| 安全-S1 SQL注入 | `security-checklist.md` S1 | ✅ | — | 全部 SQL 使用 `#{}` 参数化，无 `${}` 拼接 |
| 安全-S2 敏感信息 | `security-checklist.md` S2 | ✅ | — | 手机号脱敏输出 `ReaderServiceImpl.java:139` |
| 安全-S3 权限校验 | `security-checklist.md` S3 | N/A | — | 本系统无认证鉴权需求（设计文档无鉴权章节） |
| 安全-S4 XSS | `security-checklist.md` S4 | N/A | — | REST API JSON 返回，无 HTML 模板渲染 |

---

## 6. Step 5 — 自定义扩展检查

> 参考 `customized-checklist.md`

| 检查项 | 结果 | 说明 |
|--------|------|------|
| 错误码规范 | ✅ | `ErrorCode` 格式 `{MODULE}_{SEQ}` 一致（BOOK_001-005, READER_001-005, BORROW_001-003, COMMON_001/999） |
| 错误码未使用 | ⚠️ | `READER_003`（读者状态不合法）仅在 updateReader 中使用，但 `BookDeleteFlag` 枚举定义后从未被引用（死代码） |
| 统一返回结构 | ✅ | 所有 Controller 返回 `Result<T>`，分页返回 `PageResult<T>` |
| MyBatis resultMap | ✅ | 三个 XML 均定义 resultMap，字段映射完整（camelCase↔snake_case） |
| 逻辑删除一致性 | ✅ | 所有查询 SQL 均 `is_deleted=0`，删除 SQL 均 `SET is_deleted=1` |
| 实体 Serializable | ✅ | Book/BorrowRecord/Reader 均实现 Serializable + serialVersionUID |
| MapperScan 路径 | ⚠️ | `LibraryApplication.java:11` `@MapperScan("com.antgroup.library.*.mapper")`——通配符 `*` 仅匹配单层包名，实际包路径 `com.antgroup.library.book.mapper` 匹配成功，但若后续有多层子包可能遗漏 |

---

## 7. 问题清单

### P0

- [ ] **P0** `book/service/impl/BookServiceImpl.java:64-80` — createBook 并发竞态：selectByIsbn→insert 无锁无唯一约束异常捕获，并发请求可绕过 ISBN 唯一校验导致重复插入（schema uk_book_isbn 兜底但异常未转为 BizException）
- [ ] **P0** `borrow/service/impl/BorrowRecordServiceImpl.java:64-80` — borrowBook 并发竞态：check stock→deductStock→insert record 链路无 `SELECT FOR UPDATE`，两个线程同时借库存=1 的书可都通过 stock>0 校验。deductStock 虽有 `stock >= #{quantity}` 条件更新保护，但若第二个线程 deductStock 返回 0 行时仅抛 BOOK_005，未回滚已通过的校验逻辑——实际可接受（deductStock 是原子 SQL），但与前置 `book.getStock()<=0` 校验存在 TOCTOU 窗口
- [ ] **P0** `borrow/service/impl/BorrowRecordServiceImpl.java:64` — borrowBook 无幂等键：用户快速重复提交可创建多条借阅记录，违反 G2.1。应增加业务幂等键（如 bookId+readerId+时间窗口去重表）或前置防重校验

### P1

- [ ] **P1** `reader/service/impl/ReaderServiceImpl.java:66-69` — createReader 并发竞态：selectByPhone→insert 有 TOCTOU 窗口（schema uk_reader_phone 兜底但 DataAccessException 未转为 BizException）
- [ ] **P1** `borrow/service/impl/BorrowRecordServiceImpl.java:75-78` — 借阅上限并发校验：countUnreturnedByReader 读后无锁，并发可突破上限=5。应在 DB 层加唯一约束或悲观锁
- [ ] **P1** `book/service/impl/BookServiceImpl.java:90-98` — updateBook 中 ISBN 变更校验链路：selectById→countUnreturned→selectByIsbn 多次读无锁，并发修改可能不一致（低概率但存在）
- [ ] **P1** `common/exception/GlobalExceptionHandler.java:48-53` — 未捕获 `DataAccessException`/`SQLException`：并发唯一约束冲突时抛出 `DuplicateKeyException`（DataAccessException 子类），会被 `handleUnexpected` 以 500 返回，应单独捕获并转为 BizException 友好提示
- [ ] **P1** `book/enums/BookDeleteFlag.java` — 死代码：枚举定义后全代码库无任何引用（logicDelete 直接使用字面量 1/0），应删除或在 logicDelete SQL 中引用
- [ ] **P1** `borrow/service/impl/BorrowRecordServiceImpl.java:123-128` — `addDays` 使用 `Calendar` API 且使用默认时区 `Calendar.getInstance()`：服务器时区不一致时 dueTime 计算可能偏移。应使用 `LocalDateTime.now(ZoneId.of("Asia/Shanghai")).plusDays(30)` 或注入 `Clock`

### P2（可选）

- [ ] **P2** `borrow/service/impl/BorrowRecordServiceImpl.java:82,111` — I004: 使用 `java.util.Date`，scan-all-rules 预扫命中。建议迁移至 `java.time.LocalDateTime`
- [ ] **P2** `reader/service/impl/ReaderServiceImpl.java:17` — A2.3: import `org.springframework.beans.BeanUtils` 未使用（convertToVO 手动赋值），应删除
- [ ] **P2** `book/service/impl/BookServiceImpl.java:157-160` — convertToVO 使用 `BeanUtils.copyProperties` 反射拷贝，性能不如手动赋值（ReaderServiceImpl 已手动赋值，风格不统一）
- [ ] **P2** `book/dto/BookVO.java:21-22` — BookVO 暴露 `stock` 和 `totalStock` 两个字段，设计文档 W01 返回视图含此两字段，但借阅操作后 stock 可变，VO 返回时可能已过期（非事务内查询），属于可接受的一致性窗口
- [ ] **P2** `LibraryApplication.java:11` — `@MapperScan("com.antgroup.library.*.mapper")` 通配符 `*` 仅匹配单层包，若后续模块包层级加深需调整

---

### 7.1 问题片段

**P0-1: BookServiceImpl.java:64-80 createBook 并发竞态**

```java
// BookServiceImpl.java:64-80
@Override
@Transactional(rollbackFor = Exception.class)
public Long createBook(BookCreateRequest request) {
    Book exists = bookMapper.selectByIsbn(request.getIsbn()); // ← 无锁读取
    if (exists != null) {
        throw new BizException(ErrorCode.BOOK_002);
    }
    // ... 并发窗口：另一线程也通过了 selectByIsbn==null 校验
    bookMapper.insert(book); // ← schema uk_book_isbn 会拦截，但抛 DuplicateKeyException 非 BizException
    return book.getId();
}
```

**P0-2: BorrowRecordServiceImpl.java:64-80 borrowBook 并发竞态 + 幂等缺失**

```java
// BorrowRecordServiceImpl.java:64-80
@Override
@Transactional(rollbackFor = Exception.class)
public Long borrowBook(BorrowRequest request) {
    Book book = bookService.getBookEntity(request.getBookId()); // ← 读无锁
    if (book.getStock() == null || book.getStock() <= 0) {     // ← TOCTOU 窗口
        throw new BizException(ErrorCode.BOOK_005);
    }
    // ...
    int unreturned = borrowRecordMapper.countUnreturnedByReader(request.getReaderId()); // ← 读无锁
    if (unreturned >= BORROW_LIMIT) {
        throw new BizException(ErrorCode.BORROW_001);
    }
    bookService.deductStock(request.getBookId(), 1); // ← 原子SQL，但前置校验已过期
    // ... insert record — 无幂等键
}
```

**P0-3: BorrowRecordServiceImpl.java:64 无幂等键**

```java
// BorrowRecordServiceImpl.java:85-93 — 无幂等防重
BorrowRecord record = new BorrowRecord();
record.setBookId(request.getBookId());
record.setReaderId(request.getReaderId());
// ... 直接 insert，快速重复提交 = 多条借阅记录
borrowRecordMapper.insert(record);
```

**P1-1: ReaderServiceImpl.java:66-69 createReader 并发竞态**

```java
// ReaderServiceImpl.java:66-69
Reader exists = readerMapper.selectByPhone(request.getPhone()); // ← 无锁读取
if (exists != null) {
    throw new BizException(ErrorCode.READER_002);
}
// ... 并发窗口
readerMapper.insert(reader); // ← uk_reader_phone 兜底，但异常未捕获
```

**P1-4: GlobalExceptionHandler.java:48-53 未捕获 DataAccessException**

```java
// GlobalExceptionHandler.java:48-53
@ExceptionHandler(Exception.class)
public ResponseEntity<Result<Void>> handleUnexpected(Exception ex) {
    log.error("系统异常", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.error(ErrorCode.COMMON_999.getCode(), ErrorCode.COMMON_999.getMessage()));
    // ← DuplicateKeyException 会走到这里，返回500 + "系统异常"，而非友好的 "手机号已存在"/"ISBN已存在"
}
```

**P1-6: BorrowRecordServiceImpl.java:123-128 addDays 使用 Calendar 默认时区**

```java
// BorrowRecordServiceImpl.java:123-128
private Date addDays(Date date, int days) {
    Calendar calendar = Calendar.getInstance(); // ← 默认时区依赖 JVM 设置
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, days);
    return calendar.getTime();
}
```

**P2-1: BorrowRecordServiceImpl.java:82,111 I004 JavaUtilDate**

```java
// BorrowRecordServiceImpl.java:82
Date now = new Date();          // ← I004: java.util.Date
Date dueTime = addDays(now, BORROW_PERIOD_DAYS); // ← I004: java.util.Date
```

**P2-2: ReaderServiceImpl.java:17 未使用 import**

```java
// ReaderServiceImpl.java:17
import org.springframework.beans.BeanUtils; // ← 未使用，convertToVO 手动赋值
```

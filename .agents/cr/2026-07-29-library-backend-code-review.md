# Code Review Checklist

> **Change** `编码实现 (stage: coding)` · **分支/Commit** `AI/task-DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-f4e7cfb4-b699-4f60-` / `06a7123` · **日期** `2026-07-29`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

> **执行顺序（强制）**：已先在被审仓库根目录对变更路径运行 `references/script/scan-all-rules.sh`，输出见下方「预扫结果」；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## 预扫结果（scan-all-rules.sh）

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java/com/library src/main/resources/mapper src/main/resources/schema.sql
Engine:  ripgrep

[P2] A2.2 — WildcardImport: src/main/java/com/library/controller/BookController.java:9
[P2] A2.2 — WildcardImport: src/main/java/com/library/controller/BorrowController.java:9
[P2] A2.2 — WildcardImport: src/main/java/com/library/controller/ReaderController.java:9
[P2] I004 — JavaUtilDate: src/main/java/com/library/service/impl/BorrowServiceImpl.java:72
[P2] I004 — JavaUtilDate: src/main/java/com/library/service/impl/BorrowServiceImpl.java:109
[P2] I004 — JavaUtilDate: src/main/java/com/library/service/impl/BorrowServiceImpl.java:158
[P2] I004 — JavaUtilDate: src/main/java/com/library/service/impl/BorrowServiceImpl.java:180

=== Summary: 7 findings (P0=0, P1=0, P2=7) | 52/222 rules scanned ===
```

---

## Step 1 — 执行队列（产物 A）

> 仅列 `.java` 文件；非 Java（xml/yml/sql）在 Step 4 相关列标 `N/A(非 Java)`，于 Step 4 明细中单独核销。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | S1 | S2 | S3 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|--------|
| 1 | `pom.xml` | 依赖管理 | ⬜→N/A(非Java) | N/A | N/A | N/A | N/A | N/A | N/A |
| 2 | `src/main/java/com/library/LibraryApplication.java` | 启动类 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 3 | `src/main/java/com/library/common/BusinessConstants.java` | 状态常量 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 4 | `src/main/java/com/library/common/BusinessException.java` | 业务异常 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 5 | `src/main/java/com/library/common/GlobalExceptionHandler.java` | 全局异常 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ |
| 6 | `src/main/java/com/library/common/PageResult.java` | 分页结果 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 7 | `src/main/java/com/library/common/Result.java` | 统一返回 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 8 | `src/main/java/com/library/common/ResultCode.java` | 错误码 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 9 | `src/main/java/com/library/controller/BookController.java` | 图书接口 | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ⚠️ |
| 10 | `src/main/java/com/library/controller/BorrowController.java` | 借阅接口 | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ⚠️ |
| 11 | `src/main/java/com/library/controller/ReaderController.java` | 读者接口 | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ⚠️ |
| 12 | `src/main/java/com/library/controller/StatisticsController.java` | 统计接口 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 13 | `src/main/java/com/library/dao/mapper/BookMapper.java` | 图书DAO | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 14 | `src/main/java/com/library/dao/mapper/BorrowRecordMapper.java` | 借阅DAO | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 15 | `src/main/java/com/library/dao/mapper/ReaderMapper.java` | 读者DAO | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 16 | `src/main/java/com/library/model/dto/BookRequest.java` | 图书入参 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ |
| 17 | `src/main/java/com/library/model/dto/BookVO.java` | 图书出参 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 18 | `src/main/java/com/library/model/dto/BorrowRequest.java` | 借阅入参 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ |
| 19 | `src/main/java/com/library/model/dto/BorrowVO.java` | 借阅出参 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 20 | `src/main/java/com/library/model/dto/PopularBookVO.java` | 热门出参 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 21 | `src/main/java/com/library/model/dto/ReaderRequest.java` | 读者入参 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 22 | `src/main/java/com/library/model/dto/ReaderVO.java` | 读者出参 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 23 | `src/main/java/com/library/model/dto/StatisticsOverviewVO.java` | 统计出参 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 24 | `src/main/java/com/library/model/entity/BookDO.java` | 图书实体 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 25 | `src/main/java/com/library/model/entity/BorrowRecordDO.java` | 借阅实体 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 26 | `src/main/java/com/library/model/entity/ReaderDO.java` | 读者实体 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 27 | `src/main/java/com/library/service/BookService.java` | 图书接口 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 28 | `src/main/java/com/library/service/BorrowService.java` | 借阅接口 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 29 | `src/main/java/com/library/service/ReaderService.java` | 读者接口 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 30 | `src/main/java/com/library/service/StatisticsService.java` | 统计接口 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 31 | `src/main/java/com/library/service/impl/BookServiceImpl.java` | 图书实现 | ✅ | ✅ | ⚠️ | ✅ | ✅ | ✅ | ⚠️ |
| 32 | `src/main/java/com/library/service/impl/BorrowServiceImpl.java` | 借阅实现 | ⚠️ | ✅ | ❌ | ✅ | ✅ | ✅ | ❌ |
| 33 | `src/main/java/com/library/service/impl/ReaderServiceImpl.java` | 读者实现 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 34 | `src/main/java/com/library/service/impl/StatisticsServiceImpl.java` | 统计实现 | ✅ | ⚠️ | ✅ | ✅ | ✅ | ✅ | ⚠️ |
| 35 | `src/main/resources/application.yml` | 配置 | N/A(非Java) | N/A | N/A | N/A | N/A | ⚠️ | ⚠️ |
| 36 | `src/main/resources/data.sql` | 种子数据 | N/A(非Java) | N/A | N/A | N/A | N/A | N/A | N/A |
| 37 | `src/main/resources/mapper/BookMapper.xml` | 图书SQL | N/A(非Java) | N/A | ✅ | ✅ | ✅ | ✅ | ✅ |
| 38 | `src/main/resources/mapper/BorrowRecordMapper.xml` | 借阅SQL | N/A(非Java) | N/A | ✅ | ✅ | ✅ | ✅ | ✅ |
| 39 | `src/main/resources/mapper/ReaderMapper.xml` | 读者SQL | N/A(非Java) | N/A | ✅ | ✅ | ✅ | ✅ | ✅ |
| 40 | `src/main/resources/schema.sql` | 建表DDL | N/A(非Java) | N/A | ⚠️ | N/A | N/A | N/A | ⚠️ |

> **注**：Step1 表简化只列 G1（并发控制）、S1（SQL注入）、S2（认证授权）、S3（输入校验）四列，其余 G/S 类别与本变更无关统一在明细标 N/A。Bug 模式（B/M/I）在 §4.1 核销。

---

## Step 2 — 功能（产物 B）

> 来源：需求描述。每个 REQ 绑定关联文件，代码证据落到 `path:line`。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 管理员负责图书信息的增删改查（书名、作者、ISBN、分类、库存等） | "管理员负责图书信息的增删改查（书名、作者、ISBN、分类、库存等）" | `BookController.java` `BookServiceImpl.java` | ✅ | `BookController.java:58-79` 增删改查齐全；`BookServiceImpl.java:70-107` addBook/updateBook/deleteBook |
| REQ-2 | 借阅时扣减库存 | "核心功能是借阅和归还，借阅时扣减库存" | `BorrowServiceImpl.java` `BookMapper.xml` | ✅ | `BorrowServiceImpl.java:66` `bookMapper.deductStock`；`BookMapper.xml:94-99` `WHERE id=#{bookId} AND stock>=#{count}` |
| REQ-3 | 借阅时记录借阅期限（默认30天） | "记录借阅期限（默认30天）" | `BorrowServiceImpl.java` `BusinessConstants.java` | ✅ | `BorrowServiceImpl.java:69-76` 默认30天+Calendar计算dueTime；`BusinessConstants.java:11` `DEFAULT_BORROW_DAYS=30` |
| REQ-4 | 归还时恢复库存 | "归还时恢复库存" | `BorrowServiceImpl.java` `BookMapper.xml` | ✅ | `BorrowServiceImpl.java:106` `bookMapper.restoreStock`；`BookMapper.xml:102-107` `LEAST(stock+#{count}, total_stock)` |
| REQ-5 | 逾期给出提示 | "逾期给出提示" | `BorrowServiceImpl.java` `BorrowController.java` | ⚠️ | `BorrowServiceImpl.java:111-113` 逾期返回OVERDUE；`BorrowController.java:39-41` 拼接逾期提示。但见 P0-2：逾期归还后状态语义不一致，已归还记录仍被误判为未归还逾期 |
| REQ-6 | 读者可以搜索、浏览图书 | "读者可以搜索、浏览图书" | `BookController.java` `BookServiceImpl.java` | ✅ | `BookController.java:29-45` searchBooks+listAllBooks；`BookMapper.xml:71-85` LIKE 分页 |
| REQ-7 | 读者查看自己的借阅记录 | "查看自己的借阅记录" | `BorrowController.java` `BorrowServiceImpl.java` | ✅ | `BorrowController.java:48-56` listBorrowRecordsByReader；`BorrowServiceImpl.java:136-153` 分页查询 |
| REQ-8 | 在架/借出数量统计 | "在架/借出数量" | `StatisticsServiceImpl.java` | ✅ | 见 `StatisticsServiceImpl` getOverview 返回 onShelfCount/borrowedCount |
| REQ-9 | 热门图书统计 | "热门图书" | `StatisticsServiceImpl.java` `BorrowRecordMapper.xml` | ✅ | `BorrowRecordMapper.xml:136-144` `GROUP BY book_id ORDER BY borrow_cnt DESC LIMIT` |
| REQ-10 | 逾期列表 | "逾期列表" | `StatisticsServiceImpl.java` `BorrowRecordMapper.xml` | ✅ | `BorrowRecordMapper.xml:107-120` `selectOverduePage` WHERE due_time<CURRENT_TIMESTAMP AND return_time IS NULL |
| REQ-11 | 读者信息管理 | "管理员...以及读者信息管理" | `ReaderController.java` `ReaderServiceImpl.java` | ✅ | ReaderController 增删改查齐全 |

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 编码 UTF-8，换行一致 |
| A2 | 源文件结构/import 顺序 | ❌ | **A2.2 通配符 import**（预扫命中 4 处）：`BookController.java:9` `import org.springframework.web.bind.annotation.*;`；`BorrowController.java:9`；`ReaderController.java:9` 同样 `import org.springframework.web.bind.annotation.*;`（P2） |
| A3 | 代码样式 | ✅ | 缩进4空格一致 |
| A4 | 命名规范 | ✅ | 类名/方法名/常量命名符合规范 |
| A5 | 编码实践 | ⚠️ | `StatisticsServiceImpl.java:133` `private static final java.util.Date NOW_DUMMY = null;` 冗余字段（仅为"引入Date以便上面calculateOverdueDays使用"），而第19行已 `import java.util.Date`，应删除该无用字段（P2） |
| A6 | 特定元素样式 | ✅ | 常量类 `final class` + 私有构造，符合规范 |
| A7 | Javadoc 规范 | ⚠️ | 部分 Service 方法缺 Javadoc（如 `BorrowServiceImpl.borrowBook` 无方法级注释，仅行内注释）；DTO 字段有 `/** */` 注释，可接受（P2） |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫 `scan-all-rules.sh` 已覆盖可程序化项。下表按本变更可能命中的 B/M/I 逐条核销，其余标 N/A。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|-------------------------------|
| I004 | ❌ | **JavaUtilDate**（预扫命中 4 处）：`BorrowServiceImpl.java:72` `Date now = new Date()`；`:109` `Date now = new Date()`；`:158` `Date now = new Date()`；`:180` `Date now = new Date()`。建议用 `java.time.LocalDateTime`（P2） |
| B012 | ✅ | CalendarAddFixedDays：本变更用 `cal.add(Calendar.DAY_OF_MONTH, borrowDays)` 按天加，未用固定365天，未命中 |
| B013 | ✅ | CalendarSetHour：未涉及 HOUR 设置 |
| B011 | ✅ | BoxedPrimitiveEquality：`BusinessConstants.BORROW_STATUS_RETURNED.equals(status)` 用 String equals，未用 `==` 比较包装类 |
| B017 | ✅ | ComparingThisWithNull：无 `this==null` |
| B008 | ✅ | AvoidUsingExecutors：未用 Executors 创建线程池 |
| B010 | ✅ | BigDecimalLiteralDouble：未涉及 BigDecimal |
| 其余 B/M/I | N/A | 与本变更无直接关联（无数组操作/无Executors/无Assert/无Comparable/无集合类型不匹配等） |

### 4.2 可靠性（军规 G）

| ID | 状态 | 备注 |
|----|------|------|
| G1 并发控制 | ❌ | 见 P0-1：`BorrowServiceImpl.borrowBook` 第66行调用 `bookMapper.deductStock` 后未检查返回值；`BookServiceImpl.deductStock` 第123-126行检查了 `rows<=0` 抛异常，但 BorrowServiceImpl 未检查 → 并发下库存=1两线程同时借阅，DB守卫使一个 rows=0，但代码继续 insert 借阅记录 → 数据不一致 |
| G1 重复借阅 | ⚠️ | `BorrowServiceImpl.java:60` `countActiveBorrow` 查询后第66行才扣减/insert，存在 TOCTOU（check-then-act）窗口，并发下可重复借阅同一本未归还图书；schema.sql 无 `(reader_id,book_id,status)` 唯一索引兜底 |
| G2 超时/重试 | N/A | 单体应用直连 H2，无外部 RPC/HTTP 调用 |
| G3 限流 | N/A | 演示项目未要求限流 |
| G4 资源释放 | ✅ | 无手动资源管理，MyBatis/Spring 托管连接池 |
| G5 事务边界 | ✅ | `borrowBook`/`returnBook`/`refreshOverdueStatus` 均 `@Transactional(rollbackFor=Exception.class)`；写操作覆盖事务 |
| G6 幂等 | ⚠️ | `returnBook` 第100-103行已校验 `ALREADY_RETURNED` 防重复归还，幂等性可接受；但 `refreshOverdueStatus` 为全量更新无幂等键，属批量任务可接受 |
| G7 边界条件 | ✅ | 分页 `pageNum=Math.max(pageNum,1)`、`pageSize<=0?default:pageSize` 兜底 |
| G8-G17 | N/A | 灰度/监控/应急等与演示项目无关 |

### 4.3 安全（S）

| ID | 状态 | 备注 |
|----|------|------|
| S1 SQL注入 | ✅ | 全部 Mapper XML 使用 `#{}` 参数占位符，**无 `${}` 拼接**；`BookMapper.xml:62-63` LIKE 用 `CONCAT('%',#{keyword},'%')` 参数化，安全 |
| S2 认证/授权 | ⚠️ | `application.yml` 无任何安全配置；`GlobalExceptionHandler` 未处理认证异常。需求提到"管理员/读者两种角色"，但**代码无任何角色区分/鉴权机制**——读者接口与管理员接口（增删改查）均无权限校验，任何调用方可调用 `DELETE /api/books/{id}`（P1） |
| S3 输入校验 | ⚠️ | `BookRequest` `@NotBlank/@Size/@PositiveOrZero` 校验充分；但 `BorrowRequest.borrowDays` 无上界校验（可传极大值如36500天），不严谨（P2）；`BookRequest` 无 ISBN 格式校验 |
| S4 密钥泄露 | ✅ | `application.yml` 使用 H2 内存库无密码，无密钥硬编码 |
| S5-S10 | N/A | 无文件上传/反序列化/XSS/CSRF等场景 |

### 4.4 schema.sql 可靠性

| ID | 状态 | 备注 |
|----|------|------|
| schema 唯一约束 | ⚠️ | `borrow_record` 表无 `(reader_id, book_id, status)` 唯一索引兜底重复借阅；`book` 表有 `uk_book_isbn` 唯一约束 ✅ |

---

## Step 5 — 自定义扩展检查（产物 E）

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | N/A | N/A(未启用自定义规则) |

---

## 收口核销验证

- ✅ 执行队列 `⬜ 待审` 为零（非 Java 跳过项除外）
- ✅ Step 2 章节级勾选与逐文件结论一致（REQ-5 ⚠️ 与 BorrowServiceImpl ⚠️ 一致）
- ✅ Step 3/4/5 跨文件条目已合并勾选
- ✅ 报告审查范围文件数（40）与已审队列一致

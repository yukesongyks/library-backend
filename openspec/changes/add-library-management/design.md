# Design: add-library-management

## 跨库架构
```
+-------------------+        HTTP/JSON        +-------------------+
| library-frontend  |  -------------------->  | library-backend   |
| (页面 + HTTP客户端)|  <-------------------- | (REST API + 服务层)|
+-------------------+   统一 DTO / 状态码       +-------------------+
                                                        |
                                              进程内内存仓储（MVP）
```
- 前后端通过 REST + JSON 通信，单一契约来源为本文件「API 契约」段。
- 后端为数据模型与 API 的权威定义方；前端必须按本契约实现，不得自行扩展或改名字段。
- MVP 持久化使用进程内存储（内存 Map），不引入数据库，重启数据丢失可接受（非目标已声明）。

## 数据模型（后端权威）

### Book
| 字段 | 类型 | 说明 |
|------|------|------|
| id | integer | 主键，后端生成，自增 |
| title | string | 非空 |
| author | string | 非空 |
| totalCopies | integer | >=1，馆藏总副本数 |
| availableCopies | integer | >=0，可借副本数，初始=totalCopies |
| status | enum | `AVAILABLE`（availableCopies>0） / `UNAVAILABLE`（availableCopies==0），由后端派生 |

不变式：`0 <= availableCopies <= totalCopies`；存在未归还借阅时 `availableCopies < totalCopies`。

### BorrowRecord
| 字段 | 类型 | 说明 |
|------|------|------|
| id | integer | 主键，后端生成，自增 |
| bookId | integer | 外键 -> Book.id |
| borrower | string | 非空 |
| borrowedAt | ISO8601 string | 借出时间，后端生成 |
| returnedAt | ISO8601 string \| null | 归还时间，未归还为 null |
| status | enum | `BORROWED` / `RETURNED` |

## API 契约（前后端对齐单一来源）

基础约定：
- 内容类型 `application/json; charset=utf-8`。
- 错误体统一：`{ "error": "<message>" }`。
- 所有 `id` 为正整数。

| 方法 | 路径 | 请求体 | 成功响应 | 失败响应 |
|------|------|--------|----------|----------|
| POST | /api/books | `{title,author,totalCopies}` | 201 Book | 400 参数非法 |
| GET | /api/books | - | 200 Book[] | - |
| GET | /api/books/{id} | - | 200 Book | 404 |
| PUT | /api/books/{id} | `{title,author,totalCopies}` | 200 Book | 404 / 400 / 409 totalCopies<已借出数 |
| DELETE | /api/books/{id} | - | 204 | 404 / 409 有未归还借阅 |
| POST | /api/books/{id}/borrow | `{borrower}` | 200 BorrowRecord | 404 / 409 库存不足 |
| POST | /api/books/{id}/return | `{borrowId}` | 200 BorrowRecord | 404 / 409 记录无效 |

### 关键业务规则
- 借出：`availableCopies--`（前置 `availableCopies>=1`），生成 `status=BORROWED` 记录。
- 归还：记录存在且 `status==BORROWED` → `status=RETURNED`、`returnedAt=now`、`availableCopies++`（不超过 `totalCopies`）。
- 删除：仅当 `availableCopies==totalCopies`（无未归还借阅）允许删除。
- 状态派生：`Book.status` 不接受前端写入，由 `availableCopies` 派生。

## 跨库对齐点
1. **DTO 字段名与类型**：前端不得省略/改写 `availableCopies`、`status` 等；枚举值大小写一致（`AVAILABLE`/`UNAVAILABLE`/`BORROWED`/`RETURNED`）。
2. **状态码语义**：前端按本表区分 404（不存在）/409（业务冲突）/400（参数）做不同提示。
3. **错误体结构**：前端统一解析 `error` 字段展示，不依赖 HTTP reason phrase。
4. **ID 类型**：前端按整数处理 `id`/`bookId`/`borrowId`，不做字符串拼接歧义。

## 技术栈假设（未决，需 openspec-apply 阶段确认）
- 后端：建议 Node.js + Express（与前端同语言，降低跨语言成本）；备选 Java + Spring Boot。
- 前端：建议 React（Vite 脚手架）或 Vue 3。
- 本提案不锁定具体框架，但 API 契约与数据模型与框架无关，实现阶段选定即可。

## 安全/兼容
- 向后兼容：本变更为全新新增，无既有 API 需兼容。
- 安全：MVP 无鉴权（非目标）；实现阶段应避免 SQL/命令注入（虽用内存存储，仍保持输入校验：`title`/`author`/`borrower` 非空且长度上限）。

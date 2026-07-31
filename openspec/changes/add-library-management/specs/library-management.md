# Spec: library-management

> 本规格定义图书管理系统的用户可见行为与验收场景。所有场景使用 Given/When/Then 表达。字段定义以 `design.md` 的 DTO 为单一来源。

## Requirement: 图书信息管理（CRUD）

### Scenario: 新增图书
- **Given** 管理员尚未录入任何图书，后端 API 可达
- **When** 调用 `POST /api/books`，请求体含 `title`、`author`、`totalCopies`（>=1）
- **Then** 返回 `201 Created`，响应体含生成的 `id`、`availableCopies` 等于 `totalCopies`、`status` 为 `AVAILABLE`；调用 `GET /api/books` 列表包含该书

### Scenario: 查询图书列表
- **Given** 已存在至少 2 本图书
- **When** 调用 `GET /api/books`
- **Then** 返回 `200 OK`，响应体为图书数组，每项含 `id`、`title`、`author`、`totalCopies`、`availableCopies`、`status`；顺序稳定（按 `id` 升序）

### Scenario: 查询单本图书详情
- **Given** 存在一本 `id=1` 的图书
- **When** 调用 `GET /api/books/1`
- **Then** 返回 `200 OK`，响应体含该图书全部字段

### Scenario: 查询不存在的图书详情
- **Given** 不存在 `id=9999` 的图书
- **When** 调用 `GET /api/books/9999`
- **Then** 返回 `404 Not Found`，错误体含 `error` 字段

### Scenario: 更新图书信息
- **Given** 存在 `id=1` 的图书，当前 `title="旧标题"`
- **When** 调用 `PUT /api/books/1`，请求体 `title="新标题"`、`author`、`totalCopies`（>=当前已借出数）
- **Then** 返回 `200 OK`，响应体 `title="新标题"`；再次 `GET /api/books/1` 确认已更新

### Scenario: 删除图书
- **Given** 存在 `id=1` 的图书且当前无借出（`availableCopies == totalCopies`）
- **When** 调用 `DELETE /api/books/1`
- **Then** 返回 `204 No Content`；再次 `GET /api/books/1` 返回 `404`

### Scenario: 删除有借出的图书被拒绝
- **Given** 存在 `id=1` 的图书，`availableCopies < totalCopies`（存在未归还借阅）
- **When** 调用 `DELETE /api/books/1`
- **Then** 返回 `409 Conflict`，图书未被删除

## Requirement: 借阅管理

### Scenario: 借出图书
- **Given** 存在 `id=1` 的图书，`availableCopies >= 1`
- **When** 调用 `POST /api/books/1/borrow`，请求体含 `borrower`（非空）
- **Then** 返回 `200 OK`，响应体为借阅记录，含 `id`、`bookId=1`、`borrower`、`borrowedAt`、`returnedAt=null`、`status="BORROWED"`；对应图书 `availableCopies` 减 1

### Scenario: 库存不足时借出被拒绝
- **Given** 存在 `id=1` 的图书，`availableCopies == 0`
- **When** 调用 `POST /api/books/1/borrow`
- **Then** 返回 `409 Conflict`，错误体说明库存不足；图书 `availableCopies` 仍为 0，不产生新借阅记录

### Scenario: 借出不存在的图书
- **Given** 不存在 `id=9999` 的图书
- **When** 调用 `POST /api/books/9999/borrow`
- **Then** 返回 `404 Not Found`

### Scenario: 归还图书
- **Given** 存在 `id=1` 的图书，且存在一条 `status="BORROWED"` 的借阅记录 `borrowId`
- **When** 调用 `POST /api/books/1/return`，请求体含 `borrowId`
- **Then** 返回 `200 OK`，借阅记录 `status="RETURNED"`、`returnedAt` 非空；对应图书 `availableCopies` 加 1（不超过 `totalCopies`）

### Scenario: 归还无效借阅记录
- **Given** `borrowId` 不存在或已 `status="RETURNED"`
- **When** 调用 `POST /api/books/1/return`
- **Then** 返回 `409 Conflict`，图书 `availableCopies` 不变

## Requirement: 前端页面行为

### Scenario: 图书列表页展示
- **Given** 后端已有图书数据，前端可访问后端 API
- **When** 用户打开图书列表页
- **Then** 页面展示所有图书的标题、作者、可借数量、状态；可点击进入详情或编辑

### Scenario: 借阅与归还操作入口
- **Given** 用户在图书列表或详情页，某图书 `availableCopies >= 1`
- **When** 用户点击「借出」并填写借阅人
- **Then** 调用 `POST /api/books/{id}/borrow`，成功后列表中该图书可借数量减 1 并刷新状态；`availableCopies==0` 时「借出」按钮置灰

### Scenario: 错误反馈
- **Given** 任意前端操作触发后端非 2xx 响应
- **When** 后端返回 4xx/5xx
- **Then** 前端展示可读错误提示（非裸状态码堆栈），不静默失败

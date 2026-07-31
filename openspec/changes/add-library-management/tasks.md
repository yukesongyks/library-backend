# Tasks: add-library-management

> 有序、可勾选的实现计划。每个任务可追溯到 `specs/library-management.md` 的场景或 `design.md` 的契约决策。实现阶段（openspec-apply）勾选，本提案阶段不勾选。

## 后端 (library-backend)

- [ ] 1. 初始化后端项目脚手架与依赖（按 design.md 技术栈假设选定 Node/Express 或 Java/Spring），配置启动入口与端口
- [ ] 2. 实现 `Book` 领域模型与内存仓储（含自增 id、`availableCopies` 不变式校验）— 对应 specs CRUD 场景
- [ ] 3. 实现 `BorrowRecord` 领域模型与内存仓储（含自增 id、`status` 流转）— 对应 specs 借阅/归还场景
- [ ] 4. 实现 `POST /api/books`（参数校验：title/author 非空、totalCopies>=1）— specs 新增图书
- [ ] 5. 实现 `GET /api/books`（按 id 升序）与 `GET /api/books/{id}`（404 处理）— specs 列表/详情
- [ ] 6. 实现 `PUT /api/books/{id}`（totalCopies>=已借出数校验，否则 409）— specs 更新图书
- [ ] 7. 实现 `DELETE /api/books/{id}`（无未归还借阅才删，否则 409）— specs 删除/拒绝删除
- [ ] 8. 实现 `POST /api/books/{id}/borrow`（库存不足 409、不存在 404）— specs 借出
- [ ] 9. 实现 `POST /api/books/{id}/return`（记录无效/已归还 409、不存在 404）— specs 归还
- [ ] 10. 实现统一错误体 `{error}` 与状态码（400/404/409）— design.md 错误契约
- [ ] 11. 后端验证：对 specs 每个 When/Then 编写接口测试（可用 supertest 或 RestAssured），覆盖 201/200/204/404/409 路径

## 前端 (library-frontend)

- [ ] 12. 初始化前端项目脚手架（React/Vite 或 Vue3），配置开发服务器与后端 API 代理/跨域
- [ ] 13. 封装 HTTP 客户端，按 design.md DTO 定义 TypeScript/JS 类型，统一解析 `error` 字段 — specs 错误反馈
- [ ] 14. 实现图书列表页（调用 GET /api/books，展示标题/作者/可借数量/状态）— specs 列表页展示
- [ ] 15. 实现图书详情/编辑页（GET/PUT /api/books/{id}）— specs 详情/更新
- [ ] 16. 实现新增图书表单（POST /api/books）— specs 新增
- [ ] 17. 实现借出/归还操作入口（库存为 0 时借出置灰，错误提示可读）— specs 借阅/归还/错误反馈
- [ ] 18. 前端验证：对列表展示与借阅/归还交互做端到端/组件测试，覆盖 2xx 与 4xx 提示路径

## 跨库对齐验证

- [ ] 19. 契约一致性校验：逐字段比对前端 HTTP 客户端类型定义与后端响应体，确认字段名/类型/枚举值一致 — design.md 跨库对齐点
- [ ] 20. 状态码语义校验：前端对 404/409/400 的分支提示与后端实际返回一致 — design.md 状态码语义
- [ ] 21. 联调冒烟：启动前后端，跑通「新增→列表→借出→归还→删除」完整路径 — specs 全部场景

## 文档
- [ ] 22. 更新两仓库 README：启动命令、端口、API 概览（非本提案代码，属文档，允许）

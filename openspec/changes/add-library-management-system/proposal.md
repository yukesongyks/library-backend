# Proposal: Add Library Management System

## Why

业务需要一套图书借阅管理系统，支持管理员维护图书与读者信息，读者完成图书检索、借阅、归还及借阅记录查询。当前 `library-backend` 与 `library-frontend` 仓库均为空，属全新系统建设，需从零定义业务能力与跨库接口契约。

## What

新增图书管理系统完整能力：

1. **图书管理（book-management）**：管理员对图书信息（书名、作者、ISBN、分类、库存）执行增删改查；ISBN 唯一；库存为可借数量。
2. **读者管理（reader-management）**：管理员对读者信息执行增删改查，维护读者启用状态。
3. **借阅流通（circulation）**：读者借阅扣减库存并记录借阅期限（默认 30 天）；归还恢复库存；逾期给出提示。
4. **图书检索（discovery）**：读者按书名/作者/ISBN/分类搜索并浏览图书，支持分页。
5. **借阅记录（borrowing-history）**：读者查看本人借阅记录及状态（借阅中/已归还/逾期）。
6. **认证授权**：管理员/读者登录鉴权，按角色区分权限。

## Impact

- `library-backend`：新增认证、图书、读者、借阅、检索、借阅记录等模块及 REST API；新增数据模型与持久化。
- `library-frontend`：新增登录、管理员后台（图书/读者管理）、读者端（检索/借阅/归还/记录）页面与 API 调用。
- 跨库契约：`design.md` 定义 REST API，前端消费。

## Assumptions（保守假设，待澄清见 Handoff）

1. 后端技术栈：Java 17 + Spring Boot 3.x；前端：React/Vue（待澄清）。
2. 认证：用户名+密码登录，签发 JWT；角色枚举 `ADMIN`/`READER`。
3. 库存语义：`stock` 表示当前可借数量；不单独建模总馆藏副本，借出即扣减，归还即恢复。
4. ISBN 唯一约束：同一 ISBN 仅一条图书记录。
5. 借阅上限：单读者同时借阅在册上限 5 本（待澄清）。
6. 续借：本期不支持续借（待澄清是否需要）。
7. 逾期处置：仅提示，不产生罚款与借阅限制（待澄清）。
8. 分类：自由文本分类字段，不做层级分类表（待澄清）。
9. 并发库存扣减：通过数据库行锁/乐观锁保证不超卖。
10. 归还后借阅记录保留历史，状态置"已归还"。

## Out of Scope

- 图书预约/排队功能。
- 逾期罚款与押金体系。
- 图书推荐与评分。
- 多分馆/多机构多租户。

## Handoff

- 变更名：`add-library-management-system`
- 新建文件：`proposal.md`、`design.md`、`tasks.md`、`specs/{book-management,reader-management,circulation,discovery,borrowing-history}/spec.md`、`openspec/project.md`、`openspec/config.yaml`。
- 待澄清/假设见上文 Assumptions。
- 后续指令：审批通过后执行 `openspec-apply`（或等价实现阶段）将提案落地为代码；前端任务见 `tasks.md` 中 `[library-frontend]` 前缀项。

# Project: Library Management System

图书管理系统，前后端分离架构。

## Repositories

- `library-backend`：后端业务服务，承载图书管理、读者管理、借阅流通、检索与借阅记录等核心业务逻辑与 REST API。
- `library-frontend`：前端展示层，消费后端 REST API，提供管理员与读者两类界面。

## Scope

- 角色：管理员、读者。
- 管理员：图书信息增删改查（书名、作者、ISBN、分类、库存）、读者信息管理。
- 读者：搜索/浏览图书、发起借阅、归还、查看本人借阅记录。
- 借阅默认期限 30 天，归还恢复库存，逾期给出提示。

## Conventions

- 变更提案统一在 `library-backend` 仓库的 `openspec/changes/` 下集中管理。
- 前端相关实现任务在 `tasks.md` 中以 `[library-frontend]` 前缀标注。
- 跨库接口契约以 REST API 形式定义于 `design.md`，变更须保持后向兼容（新增字段/接口优先）。

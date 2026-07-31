# Proposal: add-library-management

## Why
当前 `library-frontend` 与 `library-backend` 两个仓库均为空仓库（仅含 README.md）。用户需求是「实现一个简单的图书管理系统」。本提案在进入编码实现前，先以 OpenSpec 规格（proposal / specs / design / tasks）固化需求范围、数据模型与跨库 API 契约，使后续 `openspec-apply` 阶段可由另一个 agent 直接落地，而无需读取聊天记录。

## What Changes
引入一个最小可用的图书管理系统，覆盖：
- 图书信息管理（CRUD）：新增、查询列表、查询详情、更新、删除图书。
- 借阅管理：借出图书、归还图书，维护图书库存与借阅状态。
- 跨库协同：后端 `library-backend` 提供 REST API，前端 `library-frontend` 提供页面调用后端 API。

变更范围（高粒度）：
- 后端：新增图书与借阅的领域模型、REST 控制器、服务层、持久化（内存存储即可，简单系统）。
- 前端：新增图书列表页、图书详情/编辑页、借阅/归还操作入口，通过 HTTP 调用后端。

## Non-Goals（明确排除）
- 不做用户认证 / 权限 / 多租户。
- 不做图书 ISBN 外部数据源接入 / 联机检索。
- 不做罚款、预约、续借、逾期提醒等高级借阅规则。
- 不做持久化数据库迁移；MVP 使用进程内存储，重启后数据丢失可接受。
- 不做生产级可观测性、限流、鉴权中间件。

## Affected Areas
- 仓库 `library-backend`：
  - 新增项目脚手架与依赖配置。
  - 新增领域模型 `Book`、`BorrowRecord`。
  - 新增 REST API：`/api/books`（CRUD）、`/api/books/{id}/borrow`、`/api/books/{id}/return`。
  - 新增服务层与内存仓储。
- 仓库 `library-frontend`：
  - 新增项目脚手架与依赖配置。
  - 新增图书列表、详情、编辑页面/组件。
  - 新增调用后端 API 的 HTTP 客户端封装。
- 跨库契约：API 请求/响应 JSON 结构与状态码在 `design.md` 统一定义，前后端必须对齐。

## Risk
- **跨库契约漂移**：前端字段与后端字段不一致导致功能不可用。缓解：`design.md` 以单一来源定义 DTO，tasks 中明确「前端按 DTO 实现、后端按 DTO 实现」并设一致性校验任务。
- **技术栈未约束**：需求未指定前后端语言/框架。本提案按「常见最小栈」假设（后端 Node.js/Express 或 Java/Spring；前端 React/Vue），最终由 `openspec-apply` 阶段在实现时确认。属未决假设，见末尾。
- **无 openspec CLI**：环境未安装 `openspec` CLI，`openspec status` 无法运行。缓解：以文件存在性与人工自审替代 CLI 校验（见 tasks 验证段）。

## Rollout / Rollback
- Rollout：作为全新功能在空仓库上新增，无既有功能受影响。
- Rollback：删除本变更新增的目录与文件即可回退到仅 README 的初始状态；无数据迁移需回滚。

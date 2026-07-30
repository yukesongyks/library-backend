# Tasks: add-library-management-system

> 标注规则：`[library-backend]` 后端任务；`[library-frontend]` 前端任务；`[cross]` 跨库对齐。
> 当前阶段为需求澄清/提案，此任务清单为实现阶段（openspec-apply）提供，非本次执行。

## 1. 后端基座 [library-backend]
- [ ] 1.1 初始化 Spring Boot 项目结构与依赖（web/validation/data-jpa/security）
- [ ] 1.2 配置关系型数据库连接与迁移脚本（book/reader/borrow_record 建表）
- [ ] 1.3 全局异常处理、统一响应体、分页响应结构

## 2. 认证授权 [library-backend]
- [ ] 2.1 登录接口 POST /api/auth/login，签发 JWT
- [ ] 2.2 JWT 过滤器与角色权限注解（ADMIN/READER）
- [ ] 2.3 GET /api/auth/me

## 3. 图书管理 [library-backend]
- [ ] 3.1 Book 实体/仓储/服务，ISBN 唯一约束
- [ ] 3.2 管理员 CRUD：GET/POST/PUT/DELETE /api/admin/books
- [ ] 3.3 删除校验在册借阅（返回 409）

## 4. 读者管理 [library-backend]
- [ ] 4.1 Reader 实体/仓储/服务，username 唯一
- [ ] 4.2 管理员 CRUD：GET/POST/PUT/DELETE /api/admin/readers
- [ ] 4.3 删除校验在册借阅（返回 409）

## 5. 借阅流通 [library-backend]
- [ ] 5.1 borrow_record 实体/仓储/服务
- [ ] 5.2 POST /api/borrow：校验启用/在册上限/库存并发扣减/due_at=now+30d
- [ ] 5.3 POST /api/return：归属与状态校验/恢复库存/置 RETURNED
- [ ] 5.4 逾期判定：now>due_at 且未归还 → OVERDUE

## 6. 检索与借阅记录 [library-backend]
- [ ] 6.1 GET /api/books 分页检索（keyword 命中 title/author/isbn，可按 category 过滤）
- [ ] 6.2 GET /api/books/{id} 详情
- [ ] 6.3 GET /api/me/borrow-records 含逾期状态与提示

## 7. 前端基座 [library-frontend]
- [ ] 7.1 初始化前端项目（React/Vue，待澄清）、路由、HTTP 客户端、拦截器注入 JWT
- [ ] 7.2 登录页 + 角色路由守卫

## 8. 前端管理员端 [library-frontend]
- [ ] 8.1 图书管理页：列表/新增/编辑/删除（库存字段）
- [ ] 8.2 读者管理页：列表/新增/编辑/删除/启用切换

## 9. 前端读者端 [library-frontend]
- [ ] 9.1 图书检索浏览页（分页、分类过滤、搜索）
- [ ] 9.2 借阅/归还操作（库存为 0 禁用借阅）
- [ ] 9.3 借阅记录页（状态展示与逾期提示）

## 10. 跨库对齐 [cross]
- [ ] 10.1 契约对齐：前端按 design.md API 实现，字段 camelCase 对齐
- [ ] 10.2 联调：登录→检索→借阅→归还→记录全链路
- [ ] 10.3 逾期场景验证（构造 due_at 超期数据）

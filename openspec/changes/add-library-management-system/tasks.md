# Tasks: add-library-management-system

> 标注规则：`[library-backend]` 后端任务；`[library-frontend]` 前端任务；`[cross]` 跨库对齐。
> 实现阶段（openspec-apply）已完成代码落地，后端因 Java/Maven 工具链不可用降级为静态审查，前端已通过 `vite build` 验证。

## 1. 后端基座 [library-backend]
- [x] 1.1 初始化 Spring Boot 项目结构与依赖（web/validation/data-jpa/security）
- [x] 1.2 配置关系型数据库连接与迁移脚本（book/reader/borrow_record 建表）
- [x] 1.3 全局异常处理、统一响应体、分页响应结构

## 2. 认证授权 [library-backend]
- [x] 2.1 登录接口 POST /api/auth/login，签发 JWT
- [x] 2.2 JWT 过滤器与角色权限注解（ADMIN/READER）
- [x] 2.3 GET /api/auth/me

## 3. 图书管理 [library-backend]
- [x] 3.1 Book 实体/仓储/服务，ISBN 唯一约束
- [x] 3.2 管理员 CRUD：GET/POST/PUT/DELETE /api/admin/books
- [x] 3.3 删除校验在册借阅（返回 409）

## 4. 读者管理 [library-backend]
- [x] 4.1 Reader 实体/仓储/服务，username 唯一
- [x] 4.2 管理员 CRUD：GET/POST/PUT/DELETE /api/admin/readers
- [x] 4.3 删除校验在册借阅（返回 409）

## 5. 借阅流通 [library-backend]
- [x] 5.1 borrow_record 实体/仓储/服务
- [x] 5.2 POST /api/borrow：校验启用/在册上限/库存并发扣减/due_at=now+30d
- [x] 5.3 POST /api/return：归属与状态校验/恢复库存/置 RETURNED
- [x] 5.4 逾期判定：now>due_at 且未归还 → OVERDUE

## 6. 检索与借阅记录 [library-backend]
- [x] 6.1 GET /api/books 分页检索（keyword 命中 title/author/isbn，可按 category 过滤）
- [x] 6.2 GET /api/books/{id} 详情
- [x] 6.3 GET /api/me/borrow-records 含逾期状态与提示

## 7. 前端基座 [library-frontend]
- [x] 7.1 初始化前端项目（React 18 + Vite）、路由、HTTP 客户端、拦截器注入 JWT
- [x] 7.2 登录页 + 角色路由守卫

## 8. 前端管理员端 [library-frontend]
- [x] 8.1 图书管理页：列表/新增/编辑/删除（库存字段）
- [x] 8.2 读者管理页：列表/新增/编辑/删除/启用切换

## 9. 前端读者端 [library-frontend]
- [x] 9.1 图书检索浏览页（分页、分类过滤、搜索）
- [x] 9.2 借阅/归还操作（库存为 0 禁用借阅）
- [x] 9.3 借阅记录页（状态展示与逾期提示）

## 10. 跨库对齐 [cross]
- [x] 10.1 契约对齐：前端按 design.md API 实现，字段 camelCase 对齐
- [ ] 10.2 联调：登录→检索→借阅→归还→记录全链路
  > [降级说明] 需后端运行环境（Java 17 + Maven）启动后联调，当前环境不可用，待部署后执行。
- [ ] 10.3 逾期场景验证（构造 due_at 超期数据）
  > [降级说明] 需后端运行环境构造超期数据验证，逻辑已在 CirculationService.applyOverdue 实现并经静态审查确认。

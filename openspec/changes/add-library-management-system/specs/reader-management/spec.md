# Spec: Reader Management

> 需求来自管理员对读者信息管理。

## Requirements

### REQ-RM-001：读者字段建模
读者信息包含：姓名（name）、用户名（username）、密码（password_hash）、角色（role=READER/ADMIN）、启用状态（enabled）。
- 边界：username 唯一，重复新增返回 409。
- 密码以 hash 存储，不返回明文。

### REQ-RM-002：管理员读者增删改查
管理员可对读者执行新增、分页查询、更新、删除。
- 场景：新增读者 → 默认 role=READER, enabled=true；username 重复返回 409。
- 场景：更新读者 → 可修改 name/username/password/enabled；改 username 为已存在值返回 409。
- 场景：删除读者 → 若存在未归还借阅记录，禁止删除，返回 409。
- 场景：禁用读者 → enabled=false 后该读者不可借阅（借阅接口返回 409）。
- 权限：仅 ADMIN；非管理员访问返回 403。

## Out of Scope
- 读者自助注册（本期仅管理员创建账号）。
- 读者角色多级权限细分。

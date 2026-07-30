# Spec: Borrowing History

> 需求来自读者查看自己的借阅记录。

## Requirements

### REQ-BH-001：本人借阅记录查询
读者可查看本人全部借阅记录及状态。
- 路径：GET /api/me/borrow-records。
- 输出：记录列表，含 bookId/bookTitle/borrowAt/dueAt/returnAt/status；status ∈ {ACTIVE, RETURNED, OVERDUE}。
- 权限：仅本人记录（基于 JWT 中的 reader_id）；访问他人记录返回 403（本期无管理员查看他人记录接口，见 Out of Scope）。

### REQ-BH-002：逾期提示展示
- status=OVERDUE 的记录须附带提示文案（如"已逾期，请尽快归还"）。
- status=ACTIVE 且未逾期可展示剩余天数（待澄清是否必需）。

## Out of Scope
- 管理员查看任意读者借阅记录。
- 借阅记录导出。

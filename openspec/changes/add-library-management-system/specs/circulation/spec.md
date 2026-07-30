# Spec: Circulation (Borrow & Return)

> 需求来自借阅与归还核心功能。

## Requirements

### REQ-CR-001：借阅
读者发起借阅，系统校验并扣减库存、建立借阅记录、设置借阅期限。
- 入参：`{bookId}`。
- 前置校验（任一失败返回 409）：
  - 当前读者 enabled=true；
  - 当前读者在册借阅（status=ACTIVE/OVERDUE）数量 < 上限（默认 5，待澄清）；
  - 图书存在且 stock>0；
- 动作（原子，并发安全）：
  - stock -= 1（乐观锁/行锁，不超卖）；
  - 写入 borrow_record：borrow_at=now, due_at=now+30d, status=ACTIVE。
- 输出：借阅记录 id、due_at。

### REQ-CR-002：归还
读者归还图书，系统恢复库存并更新借阅记录。
- 入参：`{recordId}`。
- 前置校验（任一失败返回 409/403）：
  - 借阅记录归属当前读者；
  - 记录状态为 ACTIVE 或 OVERDUE；
- 动作（原子）：
  - stock += 1；
  - return_at=now, status=RETURNED。
- 逾期归还仍允许归还，归还后该记录不再计入逾期提示。

### REQ-CR-003：逾期提示
- 判定：`now > due_at` 且 status != RETURNED → status=OVERDUE。
- 借阅记录查询接口须返回逾期状态与提示（"已逾期，请尽快归还"）。
- 归还接口对 OVERDUE 记录仍允许归还（不阻断）。

### REQ-CR-004：借阅期限默认值
- 借阅期限默认 30 天，due_at = borrow_at + 30 天。

## Edge Cases
- 同一读者对同一图书可多次借阅（不同记录），无并发同书限制（待澄清）。
- 库存为 0 时借阅返回 409（库存不足）。

## Out of Scope
- 续借功能。
- 逾期罚款与押金。

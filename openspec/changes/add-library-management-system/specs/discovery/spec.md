# Spec: Discovery (Search & Browse)

> 需求来自读者搜索、浏览图书。

## Requirements

### REQ-DC-001：图书检索
读者可分页检索图书，按 keyword 命中书名/作者/ISBN，可选按 category 过滤。
- 入参：query: page(默认1), size(默认10), keyword, category。
- 输出：分页列表，每条含 id/title/author/isbn/category/stock。
- 库存为 0 的图书仍展示（前端禁用借阅）。
- 权限：需登录（READER）；未登录返回 401。

### REQ-DC-002：图书详情
读者可查看单本图书详情。
- 路径：GET /api/books/{id}。
- 不存在返回 404。

## Edge Cases
- keyword 为空时返回全部（分页）。
- 检索是否包含分类匹配（待澄清）：当前假设 category 为过滤参数而非 keyword 命中项。

## Out of Scope
- 全文检索引擎（ES 等）。
- 图书封面/简介富信息。

# library-backend
图书管理系统后端

## 技术栈
Node.js + Express，进程内内存存储（MVP，重启数据丢失）。

## 启动
```bash
npm install
npm start
# 监听 http://localhost:3001
```

## API 概览
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/books | 新增图书 |
| GET | /api/books | 图书列表 |
| GET | /api/books/:id | 图书详情 |
| PUT | /api/books/:id | 更新图书 |
| DELETE | /api/books/:id | 删除图书 |
| POST | /api/books/:id/borrow | 借出图书 |
| POST | /api/books/:id/return | 归还图书 |

错误体统一：`{ "error": "<message>" }`。状态码：201/200/204/400/404/409。

跨库契约详见 `openspec/changes/add-library-management/design.md`。

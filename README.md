# library-backend

图书管理系统后端（Node.js + TypeScript + Express）

## 接口

| 方法 | 路径 | 请求体/参数 | 响应 |
|---|---|---|---|
| GET | `/api/algorithms/helloworld` | — | `{result: "Hello, World!"}` |
| POST | `/api/algorithms/hash` | `{text, algorithm?}` | `{result, algorithm}` |
| POST | `/api/algorithms/bubble-sort` | `{numbers}` | `{result, input}` |
| GET | `/api/algorithms/export?type=` | query: helloworld/hash/bubble-sort | CSV 文件流 |

> `algorithm` 支持 md5/sha1/sha256/sha512，大小写及连字符自动归一化（如 `SHA-256` → `sha256`）。

## 异常兜底

| 场景 | 状态码 | 响应 |
|---|---|---|
| JSON 解析失败 | 400 | `{"error":"Invalid JSON in request body"}` |
| 路由未匹配 | 404 | `{"error":"Route not found"}` |
| 未捕获异常 | 500 | `{"error":"Internal server error"}`（开发环境附加 `detail`） |
| 业务校验失败 | 400 | 具体错误信息 |

## 启动

```bash
npm run build   # tsc 编译到 dist/
npm start       # node dist/index.js（默认端口 3000）
npm run dev     # ts-node 开发模式
```

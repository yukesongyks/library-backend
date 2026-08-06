# 算法演示接口 — 需求澄清与接口规约

> 阶段：clarify ｜ 日期：2026-08-06 ｜ 仓库：library-backend
> 配套前端：library-frontend/src/views/AlgorithmDemoPage.vue

---

## 1. 需求摘要

实现四个 HTTP 接口，支撑前端算法演示页三 Tab 展示及导出功能：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| HelloWorld | GET | `/api/algo/hello` | 返回固定欢迎语 |
| 哈希计算 | POST | `/api/algo/hash` | 对输入字符串做 SHA-256，返回十六进制摘要 |
| 冒泡排序 | POST | `/api/algo/bubble-sort` | 接收整数数组，返回排序后数组及排序步数 |
| 结果导出 | POST | `/api/algo/export` | 按 type+format 导出当前 Tab 结果为 CSV 或 Excel 文件流 |

## 2. 技术决策（默认值）

| 项 | 决策 |
|----|------|
| 后端框架 | Java 17 + Spring Boot 3.x |
| 哈希算法 | SHA-256 |
| 导出格式 | CSV + Excel (.xlsx)，由请求参数 `format` 指定 |
| 异常兜底 | 全局 `@RestControllerAdvice` 统一响应结构 |

## 3. 接口契约

### 3.1 GET /api/algo/hello
- Response: `{ "message": "Hello, World!" }`

### 3.2 POST /api/algo/hash
- Body: `{ "input": "string" }`
- Response: `{ "algorithm": "SHA-256", "digest": "hex-string" }`

### 3.3 POST /api/algo/bubble-sort
- Body: `{ "numbers": [int] }`
- Response: `{ "sorted": [int], "comparisons": int, "swaps": int }`

### 3.4 POST /api/algo/export
- Body: `{ "type": "hello|hash|bubble-sort", "format": "csv|xlsx", "data": object }`
- Response: `application/octet-stream`，Content-Disposition 含文件名
- `data` 结构随 `type` 变化，与对应接口 Response 一致

## 4. 异常兜底方案

| 场景 | HTTP 状态码 | 响应体 |
|------|-------------|--------|
| 参数缺失/类型错误 | 400 | `{ "error": "BAD_REQUEST", "message": "..." }` |
| 不支持的 format/type | 400 | `{ "error": "UNSUPPORTED_FORMAT", "message": "..." }` |
| 服务端未预期异常 | 500 | `{ "error": "INTERNAL_ERROR", "message": "服务器内部错误" }` |
| 请求体过大（>1MB） | 413 | `{ "error": "PAYLOAD_TOO_LARGE", "message": "..." }` |

全局异常处理器捕获 `MethodArgumentNotValidException`、`HttpMessageNotReadableException`、`IllegalArgumentException` 及兜底 `Exception`，确保所有错误响应均为 JSON 且不含堆栈信息。

## 5. 非功能约束

- 接口无状态，不依赖数据库
- 单次请求超时 ≤ 5s
- 导出文件大小上限 10MB，超限返回 413

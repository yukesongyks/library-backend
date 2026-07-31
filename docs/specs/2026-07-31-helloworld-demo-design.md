# HelloWorld Demo 设计文档

> 阶段：系分设计（brainstorming 产出）
> 日期：2026-07-31
> 状态：已确认
> 适用仓库：library-backend（核心业务库） + library-frontend（展示层）

---

## 1. 背景与目标

在图书管理系统前后端空仓库基础上，新增一组演示功能：

- 后端提供 **3 个业务接口**：helloworld、哈希算法、冒泡排序。
- 后端提供 **1 个导出接口**：支持导出各页面的展示结果。
- 前端新增 **1 个页面**，含 3 个 Tab 分别展示 3 个接口的执行结果，并提供 1 个导出按钮调用后端导出接口。

**目标**：验证前后端跨仓协同的最小可用链路（接口契约 → 调用 → 展示 → 导出）。

---

## 2. 技术决策（已确认，按默认值定稿）

| 决策项 | 选定方案 | 依据 |
|---|---|---|
| 后端技术栈 | Spring Boot 3 + Java 17 + Maven | 对齐 `dtazziboot-java-coding-standards` 数科业务 Java 规范生态 |
| 前端技术栈 | React 19 + Vite + TypeScript | 对齐 `code-review-skill`（覆盖 React 19）审查范围 |
| HTTP 通信 | REST + JSON | 前后端最小耦合，契约清晰 |
| 哈希算法 | SHA-256（固定，JDK 内置 `MessageDigest`） | 通用安全摘要算法 |
| 导出格式 | CSV，预留 `format` 参数扩展 excel | 最常见格式，契约向后兼容 |
| 导出粒度 | 按 Tab 单页导出，`tab` 参数指定 | 需求"导出各个页面的展示结果"，单页导出最直观 |

---

## 3. 跨库架构

```
┌──────────────────────────┐         ┌──────────────────────────┐
│  library-frontend (React)│         │ library-backend (Spring) │
│                          │  HTTP   │                          │
│  DemoPage                │ ──────> │  DemoController          │
│   ├─ Tab: HelloWorld     │  JSON   │   ├─ helloworld()        │
│   ├─ Tab: Hash           │         │   ├─ hash()              │
│   └─ Tab: BubbleSort     │         │   └─ bubbleSort()        │
│                          │         │  ExportController        │
│  ExportButton ───────────┼────────>│   └─ export(tab,format)  │
│   (下载 CSV)             │  CSV    │                          │
└──────────────────────────┘         └──────────────────────────┘
```

**跨仓对齐点**：
- 接口路径前缀统一 `/api/demo`。
- 响应统一 `Result<T>` 包装：`{ code: number, message: string, data: T }`。
- 导出接口返回二进制流（`text/csv`），不走 `Result` 包装。
- 前端通过 Vite proxy 代理 `/api` → 后端，避免跨域。

---

## 4. 后端接口契约（library-backend）

### 4.1 统一响应体

```java
public record Result<T>(int code, String message, T data) {
    public static <T> Result<T> success(T data) { return new Result<>(0, "success", data); }
    public static <T> Result<T> error(int code, String message) { return new Result<>(code, message, null); }
}
```

### 4.2 HelloWorld 接口

- **方法/路径**：`GET /api/demo/helloworld`
- **入参**：无
- **响应 data**：`{ "message": "Hello, World!" }`

### 4.3 哈希算法接口

- **方法/路径**：`POST /api/demo/hash`
- **请求体**：`{ "text": "需计算哈希的文本" }`
- **响应 data**：

```json
{
  "original": "需计算哈希的文本",
  "algorithm": "SHA-256",
  "digest": "64位十六进制摘要"
}
```

### 4.4 冒泡排序接口

- **方法/路径**：`POST /api/demo/bubble-sort`
- **请求体**：`{ "numbers": [5, 2, 9, 1, 5, 6] }`
- **响应 data**：

```json
{
  "input": [5, 2, 9, 1, 5, 6],
  "sorted": [1, 2, 5, 5, 6, 9],
  "swaps": 8
}
```

- **实现**：手写冒泡排序（非 `Arrays.sort`），统计交换次数。

### 4.5 导出接口

- **方法/路径**：`GET /api/demo/export`
- **Query 参数**：

| 参数 | 必填 | 取值 | 说明 |
|---|---|---|---|
| `tab` | 是 | `helloworld` \| `hash` \| `bubble-sort` | 指定导出哪个 Tab 的结果 |
| `format` | 否 | `csv`（默认） | 预留，未来可扩展 `excel` |

- **响应**：`Content-Type: text/csv; charset=UTF-8` + `Content-Disposition: attachment; filename=<tab>-result.csv`
- **各 Tab 导出内容**：
  - `helloworld`：单列 `message`
  - `hash`：三列 `original,algorithm,digest`
  - `bubble-sort`：两列 `index,value`
- **向后兼容**：`format` 未识别值回退 `csv`，响应头标注 `X-Export-Fallback: csv`。

---

## 5. 异常兜底方案

### 5.1 统一错误码

| code | 含义 | 触发场景 |
|---|---|---|
| `0` | 成功 | 正常返回 |
| `40001` | 参数校验失败 | 必填缺失、格式非法、数组越界 |
| `40002` | 业务规则拒绝 | 空文本/空数组、超长输入 |
| `50000` | 系统内部错误 | 未捕获异常兜底 |

### 5.2 后端兜底（library-backend）

通过 `@RestControllerAdvice` 全局异常处理器统一拦截，所有业务接口异常均走 `Result.error()` 返回（HTTP 200 + 非 0 code），导出接口异常返回 HTTP 500 + JSON 错误体。

**5.2.1 全局异常分层处理**

| 异常类型 | 处理器 | 返回 |
|---|---|---|
| `MethodArgumentNotValidException`（@Valid 校验失败） | 参数校验处理器 | `Result.error(40001, 字段错误描述)` |
| `IllegalArgumentException`（业务前置校验） | 业务异常处理器 | `Result.error(40002, 原因)` |
| `ConstraintViolationException`（Query 参数校验） | 参数校验处理器 | `Result.error(40001, 错误描述)` |
| `Exception`（兜底） | 未知异常处理器 | `Result.error(50000, "系统繁忙，请稍后重试")` |

**5.2.2 各接口输入校验与兜底**

| 接口 | 校验规则 | 异常处理 |
|---|---|---|
| hash | `text` 非空且长度 ≤ 1000 | 空文本 → `40002 "text 不能为空"`；超长 → `40002 "text 长度超限(≤1000)"` |
| bubble-sort | `numbers` 非空、长度 ≤ 1000、元素为整数 | 空数组 → `40002 "numbers 不能为空"`；超长 → `40002 "数组长度超限(≤1000)"` |
| export | `tab` ∈ 合法枚举 | 非法值 → `40001 "非法 tab 参数"`；CSV 写入 IOException → HTTP 500 + `50000` |

**5.2.3 哈希算法兜底**

- `MessageDigest.getInstance("SHA-256")` 理论不抛异常（JDK 内置），仍包裹 try-catch 兜底 `NoSuchAlgorithmException` → `50000`。
- 文本编码统一 UTF-8，摘要输出小写十六进制。

**5.2.4 导出接口兜底**

- `tab` 非法枚举值：不进入下载流，直接返回 `Result.error(40001, ...)`（JSON）。
- CSV 生成阶段 IOException：返回 HTTP 500 + `Result.error(50000, "导出失败，请稍后重试")`。
- 中文编码：写入 UTF-8 BOM 头防止 Excel 打开乱码。

### 5.3 前端兜底（library-frontend）

| 场景 | 兜底策略 |
|---|---|
| 网络请求超时/断连 | `request.ts` 设置 10s 超时，catch 后 toast 提示"网络异常，请检查后端服务" |
| 接口返回非 0 code | 统一拦截 `Result.code !== 0`，toast 显示 `message`，Tab 区显示错误占位 |
| 接口返回 500 / 解析失败 | catch 兜底，toast 提示"服务异常，请稍后重试" |
| 导出下载失败 | `window.location.href` 方式无法捕获下载失败；改用 `fetch` 流式下载，失败 toast 提示 |
| 导出返回 JSON 错误体 | 判断响应 Content-Type，非 CSV 时解析 JSON 错误码并 toast 提示 |
| 输入校验前置 | 哈希：空文本按钮 disabled；冒泡：非法数字格式前端拦截不请求 |

### 5.4 异常链路时序

```
前端请求 → 后端 Controller
              ├─ @Valid 校验失败 ──→ GlobalExceptionHandler ──→ Result.error(40001) ──→ 前端 toast
              ├─ 业务校验失败(IAE) ──→ GlobalExceptionHandler ──→ Result.error(40002) ──→ 前端 toast
              ├─ 未捕获异常 ──→ GlobalExceptionHandler ──→ Result.error(50000) ──→ 前端 toast
              └─ 正常 ──→ Result.success(data) ──→ 前端渲染
```

---

## 6. 前端页面设计（library-frontend）

### 6.1 路由与组件

- 新增路由：`/demo`
- 页面组件：`DemoPage.tsx`
- 子组件：`HelloWorldTab` / `HashTab` / `BubbleSortTab`

### 6.2 Tab 交互

| Tab | 触发接口 | 展示内容 | 输入 |
|---|---|---|---|
| HelloWorld | `GET /api/demo/helloworld` | 固定字符串 | 无 |
| 哈希算法 | `POST /api/demo/hash` | 原文 + 算法 + 摘要 | 文本输入框 |
| 冒泡排序 | `POST /api/demo/bubble-sort` | 输入/排序后/交换次数 | 数字数组（逗号分隔） |

### 6.3 导出按钮

- 位置：页面右上角，全局作用于当前选中 Tab。
- 行为：`fetch('/api/demo/export?tab=<当前tab>')` 流式下载，失败走 §5.3 兜底。
- 禁用态：Tab 结果未加载完成时 disabled。

### 6.4 网络层

- `vite.config.ts` 配置 proxy：`/api` → `http://localhost:8080`。
- 封装 `request.ts`：统一处理 `Result<T>`，非 0 code 抛错并 toast 提示，10s 超时。

---

## 7. 风险与约束

| 风险 | 缓解 |
|---|---|
| 两仓空仓库需从零搭骨架 | 编码阶段优先建工程骨架再填业务 |
| 接口契约跨仓手动对齐 | 以本文档 §4 契约为唯一事实来源 |
| CSV 含中文乱码 | 后端写入 UTF-8 BOM 头 |
| 冒泡排序大数组性能 | 入参数组长度上限 1000 |
| 未捕获异常导致 500 裸栈 | `@RestControllerAdvice` 全局兜底 `50000` |

---

## 8. 交付物清点（设计阶段）

- [x] 本设计文档（library-backend）
- [ ] 编码阶段：后端工程骨架 + 4 接口 + 全局异常处理器（library-backend）
- [ ] 编码阶段：前端工程骨架 + DemoPage + 3 Tab + 导出按钮 + 网络兜底（library-frontend）

> 当前为系分阶段，不产出代码文件。编码阶段按 §4 契约 + §5 兜底执行。

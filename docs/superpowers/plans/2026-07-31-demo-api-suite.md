# Demo API Suite Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) format. Mark each `- [x]` upon completion.
>
> 阶段：plan | 技能：/writing-plans | 日期：2026-07-31
> 依据：`.agents/clarify/clarify-result.md`（需求澄清结论已定稿）

## Feature Name

demo-api-suite —— 跨前后端通用演示功能集（三业务接口 + 三 Tab 页面 + 导出 + 埋点 + 可视化报表）

## Global Constraints

- **技术栈锁定**（澄清 D1）：前端 React 18 + TypeScript + Vite + Ant Design + ECharts；后端 Node.js + Express + TypeScript。禁止换栈。
- **契约优先 / 向后兼容**（澄清 D2）：所有接口后续扩展仅新增字段，不破坏现有字段。统一响应包络 `{ code, message?, data? }`，`code=0` 成功。
- **路径隔离**：所有产物文件写入对应仓库 worktree_path 下，严禁写入 worktree 父目录。
- **Git 只读**：禁止任何 Git 写操作，仅允许 `git status/log/diff/show`。
- **工具黑名单**：禁止使用 `grep` / `glob`。
- **异常兜底**：必须实现澄清文档第四章全部兜底策略（参数校验 400、algorithm 降级、array 校验 422、埋点失败不阻断、导出空数据表头、报表空聚合、500 统一捕获）。
- **跨库枚举对齐**：Tab 标识 `helloworld|hash|bubble-sort`、维度枚举 `type|level|dept`、请求头 `X-Caller-Id` 前后端必须一致。
- **埋点不阻断主请求**：埋点写入失败仅记录日志，不影响业务响应。
- **TS 严格类型**：无 `any`，优先 SDK/标准库类型定义（遵循 AGENTS.md TypeScript 原则）。

## File Structure

### library-backend（后端，worktree_path 下从零搭建）

```
library-backend/
├── package.json
├── tsconfig.json
├── .env.example
├── src/
│   ├── index.ts                      # Express 启动入口，端口 3000
│   ├── app.ts                        # Express app 装配，注册路由与中间件
│   ├── types/
│   │   ├── api.ts                    # 统一响应包络 ApiResponse<T> 类型
│   │   ├── metrics.ts                # 埋点/报表相关类型
│   │   └── caller.ts                # 人员维度 mock 模型类型
│   ├── middleware/
│   │   ├── metricsMiddleware.ts      # 三业务接口自动埋点中间件
│   │   ├── errorMiddleware.ts        # 统一 500 错误捕获
│   │   └── callerIdMiddleware.ts     # 解析 X-Caller-Id，默认 demo-user
│   ├── utils/
│   │   ├── response.ts               # 统一响应构造 success/fail
│   │   ├── hash.ts                   # sha256/md5 哈希计算
│   │   ├── bubbleSort.ts             # 冒泡排序算法
│   │   └── exporter.ts               # xlsx 导出工具（基于 exceljs）
│   ├── data/
│   │   ├── metricsStore.ts           # 埋点内存存储（数组 + Map 索引）
│   │   └── callerProfile.ts          # mock 人员维度模型（caller_id → type/level/dept）
│   └── routes/
│       ├── helloworldRoute.ts        # GET /api/helloworld
│       ├── hashRoute.ts              # POST /api/hash
│       ├── bubbleSortRoute.ts        # POST /api/bubble-sort
│       ├── exportRoute.ts            # GET /api/export
│       └── metricsRoute.ts           # GET /api/metrics/calls
└── docs/superpowers/plans/2026-07-31-demo-api-suite.md  # 本计划文档
```

### library-frontend（前端，worktree_path 下从零搭建）

```
library-frontend/
├── package.json
├── tsconfig.json
├── vite.config.ts                    # 含 proxy 代理 /api → localhost:3000
├── .env.example                      # VITE_API_BASE_URL
├── index.html
├── src/
│   ├── main.tsx                      # React 入口
│   ├── App.tsx                       # 根组件，含布局与路由
│   ├── types/
│   │   └── api.ts                    # 前端消费的响应类型（与后端对齐）
│   ├── api/
│   │   ├── client.ts                 # fetch 封装，注入 X-Caller-Id
│   │   ├── helloworld.ts             # GET /api/helloworld
│   │   ├── hash.ts                   # POST /api/hash
│   │   ├── bubbleSort.ts             # POST /api/bubble-sort
│   │   ├── exportApi.ts              # GET /api/export（blob 下载）
│   │   └── metrics.ts                # GET /api/metrics/calls
│   ├── hooks/
│   │   ├── useApiResult.ts           # 通用请求 hook（loading/error/data/retry）
│   │   └── useMetrics.ts             # 报表数据 hook
│   ├── components/
│   │   ├── DemoTabs.tsx              # 三 Tab 容器
│   │   ├── HelloWorldTab.tsx         # helloworld 展示
│   │   ├── HashTab.tsx               # 哈希输入 + 结果展示
│   │   ├── BubbleSortTab.tsx        # 数组输入 + 排序结果展示
│   │   ├── ExportButton.tsx          # 导出按钮（按当前 Tab）
│   │   └── MetricsReport.tsx        # 报表区（折线/饼/柱状）
│   └── styles/
│       └── app.css                   # 全局样式
```

## Implementation Plan

---

### Task 1: library-backend 工程脚手架搭建

**ID:** T1-backend-scaffold
**Goal:** 从零搭建 Node.js + Express + TypeScript 工程骨架，可编译运行。

- [ ] 1.1 创建 `package.json`（name=library-backend, type=module, scripts: dev/build/start, deps: express/typescript/tsx/exceljs/crypto, devDeps: @types/express/@types/node/ts-node）
- [ ] 1.2 创建 `tsconfig.json`（strict: true, esModuleInterop, outDir: dist, target ES2020）
- [ ] 1.3 创建 `src/types/api.ts` 定义统一响应包络 `ApiResponse<T> = { code: number; message?: string; data?: T }`
- [ ] 1.4 创建 `src/utils/response.ts`：`success<T>(data: T, message?: string)` 与 `fail(code, message)` 构造器
- [ ] 1.5 创建 `src/index.ts`（监听端口 3000）与 `src/app.ts`（app 装配骨架，预留路由挂载点）
- [ ] 1.6 创建 `.env.example`（PORT=3000）
- [ ] 1.7 创建 `src/middleware/errorMiddleware.ts` 统一 500 捕获：返回 `{ code:500, message:"internal error" }`

**Verify:** `npx tsc --noEmit` 编译通过，无类型错误。

---

### Task 2: 三个业务接口实现

**ID:** T2-business-apis
**Goal:** 实现 helloworld / 哈希算法 / 冒泡排序三个接口，含全部异常兜底。

- [ ] 2.1 **helloworld**：`src/routes/helloworldRoute.ts`，GET `/api/helloworld`，返回 `{ code:0, data:{ message:"Hello, World!" } }`
- [ ] 2.2 **哈希算法**：`src/utils/hash.ts` 实现 `computeHash(input, algorithm)`，支持 `sha256`/`md5`（用 Node `crypto`）
- [ ] 2.3 `src/routes/hashRoute.ts`，POST `/api/hash`，校验 `input` 非空（否则 400 `input is required`）；`algorithm` 非法时降级 sha256 并在 message 标注 `algorithm fallback to sha256`；返回 `{ code:0, data:{ input, algorithm, hash } }`
- [ ] 2.4 **冒泡排序**：`src/utils/bubbleSort.ts` 实现纯函数 `bubbleSort(arr: number[]): number[]`
- [ ] 2.5 `src/routes/bubbleSortRoute.ts`，POST `/api/bubble-sort`，校验 `array` 为 `number[]`（否则 422 `array must be number[]`）；空数组返回 `{ code:0, data:{ sorted:[] } }`；正常返回 `{ code:0, data:{ sorted } }`
- [ ] 2.6 在 `src/app.ts` 注册三个路由到 `/api` 前缀

**Verify:** 启动服务，curl 三个接口正常与异常场景，响应符合澄清 D2 全部契约。

---

### Task 3: 埋点机制与人员维度模型

**ID:** T3-metrics-tracking
**Goal:** 三业务接口被调用时自动埋点，记录调用次数与调用人及人员维度。

- [ ] 3.1 `src/types/metrics.ts`：定义 `CallRecord { api_name, caller_id, caller_type, caller_level, caller_dept, call_time }`
- [ ] 3.2 `src/types/caller.ts`：定义 `CallerProfile { caller_id, caller_type, caller_level, caller_dept }`
- [ ] 3.3 `src/data/callerProfile.ts`：mock 人员模型，按 `caller_id` 关联维度，默认 `demo-user` 对应一组维度值；未命中 caller 给出兜底维度 `unknown`
- [ ] 3.4 `src/middleware/callerIdMiddleware.ts`：解析请求头 `X-Caller-Id`，默认 `demo-user`，挂到 `req.callerId`
- [ ] 3.5 `src/data/metricsStore.ts`：内存存储 `CallRecord[]`，提供 `record(c)` 与 `query({dimension, range})` 聚合查询
- [ ] 3.6 `src/middleware/metricsMiddleware.ts`：仅对三业务路径埋点，写入 `metricsStore`；**写入失败 try/catch 不阻断主请求**，仅 `console.error` 记录
- [ ] 3.7 在 `src/app.ts` 挂载 `callerIdMiddleware` 与 `metricsMiddleware`（位于业务路由之前）

**Verify:** 调用任一业务接口后，`metricsStore` 中新增一条 `CallRecord`；模拟 store 抛错时业务响应仍正常返回。

---

### Task 4: 导出接口

**ID:** T4-export-api
**Goal:** 后端导出接口，按 tab 导出对应展示结果为 xlsx 二进制流。

- [ ] 4.1 `src/utils/exporter.ts`：基于 `exceljs` 实现 `buildWorkbook(tab, dataRows) -> Buffer`，含表头行
- [ ] 4.2 `src/routes/exportRoute.ts`，GET `/api/export?tab=...&format=xlsx`：校验 `tab` 合法枚举（否则 400 `invalid tab`）；按 tab 拼装对应数据行；数据为空仍生成仅表头的 xlsx
- [ ] 4.3 设置响应头 `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` 与 `Content-Disposition: attachment; filename=<tab>.xlsx`
- [ ] 4.4 在 `src/app.ts` 注册导出路由

**Verify:** curl 各 tab 导出，生成合法 xlsx 文件；非法 tab 返回 400；空数据生成仅表头文件。

---

### Task 5: 报表接口

**ID:** T5-metrics-api
**Goal:** 报表接口按人员维度聚合调用情况，供前端折线/饼/柱状图渲染。

- [ ] 5.1 `src/routes/metricsRoute.ts`，GET `/api/metrics/calls?dimension=type|level|dept&range=7d`
- [ ] 5.2 校验 `dimension` 枚举合法（否则 400 `invalid dimension`）
- [ ] 5.3 聚合 `metricsStore`：`trend`（按日调用趋势，折线图用，每天一个数值）+ `distribution`（维度占比/调用量，饼图与柱状图用，每维度一个数值）
- [ ] 5.4 无数据时返回 `{ code:0, data:{ trend:[], distribution:[] } }`
- [ ] 5.5 在 `src/app.ts` 注册报表路由

**Verify:** 调用若干业务接口产生埋点后，请求报表接口返回结构正确的聚合数据；无埋点时返回空聚合结构。

---

### Task 6: library-frontend 工程脚手架搭建

**ID:** T6-frontend-scaffold
**Goal:** 从零搭建 React 18 + TypeScript + Vite + Ant Design + ECharts 前端工程。

- [ ] 6.1 创建 `package.json`（deps: react/react-dom/antd/echarts/echarts-for-react, devDeps: vite/@vitejs/plugin-react/typescript/@types/react）
- [ ] 6.2 创建 `vite.config.ts`，含 `@vitejs/plugin-react`，proxy `/api` → `http://localhost:3000`
- [ ] 6.3 创建 `tsconfig.json`（strict, jsx: react-jsx, paths: `@/*` → `src/*`）
- [ ] 6.4 创建 `index.html` + `src/main.tsx` + `src/App.tsx`（Ant Design ConfigProvider 包裹）
- [ ] 6.5 创建 `.env.example`（`VITE_API_BASE_URL=/api`）
- [ ] 6.6 创建 `src/types/api.ts`，与后端响应类型对齐

**Verify:** `npm run dev` 启动开发服务器，页面可访问。

---

### Task 7: 前端 API 层与通用 hook

**ID:** T7-frontend-api-layer
**Goal:** 封装所有后端接口调用，统一注入 X-Caller-Id，提供通用请求 hook。

- [ ] 7.1 `src/api/client.ts`：fetch 封装，注入请求头 `X-Caller-Id`（默认 `demo-user`），解析统一响应包络，`code!==0` 抛错携带 message
- [ ] 7.2 `src/api/helloworld.ts`、`hash.ts`、`bubbleSort.ts`、`metrics.ts`：各业务接口封装函数
- [ ] 7.3 `src/api/exportApi.ts`：导出接口封装，返回 Blob，触发浏览器下载；失败不触发下载
- [ ] 7.4 `src/hooks/useApiResult.ts`：通用 hook，管理 `loading/error/data/retry`，请求失败保留上次成功结果（如有）
- [ ] 7.5 `src/hooks/useMetrics.ts`：报表数据专用 hook

**Verify:** 开发服务器中调用各 API 函数，控制台无类型错误；失败场景保留上次成功结果。

---

### Task 8: 三 Tab 页面与展示组件

**ID:** T8-demo-tabs
**Goal:** 一个页面三 Tab，分别展示三接口执行结果，含前端异常兜底。

- [ ] 8.1 `src/components/DemoTabs.tsx`：Ant Design Tabs 容器，三 Tab key 分别为 `helloworld|hash|bubble-sort`
- [ ] 8.2 `src/components/HelloWorldTab.tsx`：调用 helloworld 接口，展示 `message`；失败展示错误提示 + 重试按钮，不白屏
- [ ] 8.3 `src/components/HashTab.tsx`：输入框（input + algorithm 选择），调用 hash 接口，展示 `{ input, algorithm, hash }`；`code!==0` 展示 message
- [ ] 8.4 `src/components/BubbleSortTab.tsx`：数组输入（逗号分隔），调用 bubble-sort 接口，展示 `sorted`；失败展示 message
- [ ] 8.5 三 Tab 共享 `ExportButton`，按当前 activeTab 触发导出
- [ ] 8.6 `src/components/ExportButton.tsx`：调用 `exportApi`，成功触发下载，失败 Toast 提示不触发下载

**Verify:** 切换三 Tab 分别触发对应接口并正确展示结果；断开后端时展示错误提示 + 重试，不白屏。

---

### Task 9: 可视化报表组件

**ID:** T9-metrics-report
**Goal:** 页面内报表区，按人员维度展示调用情况，折线图 + 饼图 + 柱状图，含异常兜底。

- [ ] 9.1 `src/components/MetricsReport.tsx`：报表容器，维度切换（type/level/dept），调用 `useMetrics`
- [ ] 9.2 折线图（echarts-for-react line）：按日调用趋势，数据源 `trend`
- [ ] 9.3 饼图（pie）：维度占比，数据源 `distribution`
- [ ] 9.4 柱状图（bar）：维度调用量对比，数据源 `distribution`
- [ ] 9.5 报表加载失败：图表区域展示空状态 + 重试；图表渲染异常降级为文本列表展示
- [ ] 9.6 报表无数据：渲染空状态，不报错

**Verify:** 后端有埋点数据时三种图表正确渲染；断开后端或无数据时展示空状态 + 重试，不白屏。

---

### Task 10: 跨库契约对齐与端到端联调

**ID:** T10-e2e-alignment
**Goal:** 前后端跨库契约全链路对齐验证。

- [ ] 10.1 启动后端（端口 3000）与前端 dev server，配置 proxy 生效
- [ ] 10.2 验证 API 基址对齐：前端 `VITE_API_BASE_URL=/api` 经 proxy 到达后端
- [ ] 10.3 验证响应包络：三接口、导出、报表均消费/产出 `{ code, message?, data? }`
- [ ] 10.4 验证 Tab 标识枚举一致：`helloworld|hash|bubble-sort`
- [ ] 10.5 验证调用人对齐：前端注入 `X-Caller-Id`，后端读取并关联人员维度
- [ ] 10.6 验证维度枚举一致：`type|level|dept`
- [ ] 10.7 验证埋点联动报表：调用业务接口后报表数据更新

**Verify:** 端到端全流程跑通，无契约不一致。

---

## Rollback

- 各 Task 独立提交，回滚时按 Task 粒度 revert 对应 commit
- 后端内存埋点无持久化，重启即清空（演示态可接受）
- 前端 proxy 配置仅在 dev 环境生效，不影响生产构建

## Notes

- `cloud` 仓库作为技术栈基线参考，不参与本次改动
- `ranxitest` 仓库未采用，不参与本次改动
- 人员维度为后端 mock 模型，演示态无需对接真实身份系统
- 本计划为 plan 阶段产物，不含代码变更；代码实现由后续编码阶段按 Task 顺序执行

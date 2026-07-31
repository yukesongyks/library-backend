# 需求澄清结论 (Clarify Result)

> 阶段：clarify | 技能：/brainstorming | 日期：2026-07-31
> 状态：已定稿（确认项按默认值填写，异常兜底已补充）

## 一、需求概述

跨前后端通用演示功能集，含五块：

1. **三个后端接口**：helloworld、哈希算法、冒泡排序
2. **前端页面**：一个页面三 Tab，分别展示三接口执行结果
3. **导出功能**：前端导出按钮 + 后端导出接口，导出各 Tab 展示结果
4. **后端埋点**：记录调用次数与调用人
5. **可视化报表**：页面展示调用情况，按人员维度（类型/层级/部门），折线图 + 饼图 + 柱状图

## 二、跨仓现状（关键事实）

| 仓库 | 状态 | 关联度 |
|------|------|--------|
| `library-frontend` | 空库（仅 README），需从零搭建 | ★★★ 前端归属 |
| `library-backend` | 空库（仅 README + .agents），需从零搭建 | ★★★ 后端归属 |
| `cloud` | 成熟 TS monorepo（pnpm+oxlint+drizzle） | 技术栈基线参考 |
| `ranxitest` | Spring Boot Java 应用 | 未采用 |

## 三、定稿决策（确认项按默认值填写）

### D1 技术栈与仓库归属

| 项 | 值 |
|----|----|
| 前端归属 | `library-frontend` |
| 前端技术栈 | React 18 + TypeScript + Vite + Ant Design + ECharts |
| 后端归属 | `library-backend` |
| 后端技术栈 | Node.js + Express + TypeScript |

### D2 三个接口契约（REST，统一响应包络 `{ code, message?, data? }`，code=0 成功）

| 接口 | Method | Path | 入参 | 出参 |
|------|--------|------|------|------|
| helloworld | GET | `/api/helloworld` | 无 | `{ code:0, data:{ message:"Hello, World!" } }` |
| 哈希算法 | POST | `/api/hash` | `{ input: string, algorithm?: "sha256"|"md5" }` | `{ code:0, data:{ input, algorithm, hash } }` |
| 冒泡排序 | POST | `/api/bubble-sort` | `{ array: number[] }` | `{ code:0, data:{ sorted: number[] } }` |

> 契约优先：后续扩展仅新增字段，不破坏现有字段。

### D3 埋点机制

- **时机**：三业务接口被调用时由中间件自动埋点
- **字段**：`api_name`、`caller_id`、`caller_type`(人员类型)、`caller_level`(人员层级)、`caller_dept`(人员部门)、`call_time`
- **调用人识别**：请求头 `X-Caller-Id`，默认 `demo-user`
- **人员维度来源**：后端自建 mock 人员模型，按 `caller_id` 关联维度

### D4 导出接口（默认 xlsx）

- **接口**：`GET /api/export?tab=helloworld|hash|bubble-sort&format=xlsx`
- **响应**：二进制流，`Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **内容**：对应 Tab 当前展示结果数据

### D5 报表接口与图表（默认近 7 天）

- **接口**：`GET /api/metrics/calls?dimension=type|level|dept&range=7d`
- **维度**：人员类型、人员层级、人员部门
- **图表**：折线图（按日调用趋势）+ 饼图（维度占比）+ 柱状图（维度调用量对比）

## 四、异常兜底方案

### 4.1 后端异常兜底

| 场景 | 兜底策略 | 返回示例 |
|------|---------|---------|
| 参数缺失/格式错误 | 请求体校验失败，返回 `code=400` | `{ code:400, message:"input is required" }` |
| hash algorithm 非法 | 降级为 `sha256` 并在 message 标注 | `{ code:0, message:"algorithm fallback to sha256", data:{...} }` |
| bubble-sort array 非数组/含非数字 | 返回 `code=422` | `{ code:422, message:"array must be number[]" }` |
| bubble-sort 数组为空 | 返回空数组，`code=0` | `{ code:0, data:{ sorted:[] } }` |
| 埋点写入失败 | 不阻断主请求，仅记录错误日志 | 主响应正常返回 |
| 导出 tab 非法 | 返回 `code=400` | `{ code:400, message:"invalid tab" }` |
| 导出数据为空 | 仍生成 xlsx（仅表头），`code=0` | 二进制流 + 表头行 |
| 报表无数据 | 返回空聚合结构，前端图表渲染空状态 | `{ code:0, data:{ trend:[], distribution:[] } }` |
| 未知 500 错误 | 统一捕获，返回 `code=500` | `{ code:500, message:"internal error" }` |

### 4.2 前端异常兜底

| 场景 | 兜底策略 |
|------|---------|
| 接口请求失败（网络/超时） | Tab 内展示错误提示 + 重试按钮，不白屏 |
| 接口返回 code≠0 | 展示后端 message，保留上次成功结果（如有） |
| 导出请求失败 | Toast 提示失败，不触发文件下载 |
| 报表数据加载失败 | 图表区域展示空状态 + 重试 |
| 图表渲染异常 | 图表容器降级为文本列表展示 |

## 五、跨库契约对齐点

| 契约点 | 前端(library-frontend) | 后端(library-backend) | 对齐 |
|--------|------------------------|----------------------|------|
| API 基址 | `VITE_API_BASE_URL` 代理 | `http://localhost:3000` | 环境变量解耦 |
| 响应包络 | 消费 `{ code, data }` | 产出 `{ code, message?, data? }` | code=0 成功 |
| Tab 标识 | `helloworld\|hash\|bubble-sort` | 导出 `tab` 参数同值 | 枚举一致 |
| 调用人 | 请求头 `X-Caller-Id` | 读取 `X-Caller-Id` | 演示态 mock |
| 维度枚举 | `type\|level\|dept` | `dimension` 参数同值 | 枚举一致 |

## 六、阶段产出清点

| 产物 | 路径 | 类型 |
|------|------|------|
| 本澄清结论 | `[library-backend] .agents/clarify/clarify-result.md` | 设计文档 |
| 代码文件 | — | 无（clarify 阶段禁止修改代码） |

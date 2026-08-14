# 跨仓功能设计文档：API 演示 + 数据看板

> 日期: 2025-01-13
> 状态: 初稿

## 1. 概述

为图书管理系统新增三个功能模块：
- 后端提供三个示例接口（HelloWorld、哈希算法、冒泡排序）
- 前端新增页面，含三个 Tab 分别展示各接口执行结果
- 后端提供导出接口，支持导出各页面展示结果
- 后端埋点统计调用次数和调用人信息
- 前端可视化报表展示调用情况（折线图、饼图、柱状图），支持按人员类型/层级/部门维度筛选

## 2. 技术栈

| 层 | 技术 | 版本 |
|----|------|------|
| 前端框架 | Vue 3 | Composition API |
| UI 组件库 | Element Plus | 最新 |
| 图表库 | ECharts 5 | 最新 |
| 构建工具 | Vite | 最新 |
| 后端框架 | Spring Boot 3 | JDK 17 |
| 数据库 | H2 (开发) / MySQL (生产) | - |
| API 风格 | RESTful JSON | - |

## 3. 架构总览

```
┌─────────────────────────────────────────────────┐
│                 前端 (Vue3)                       │
│  ┌───────────────────────────────────────────┐  │
│  │  API 演示页面                               │  │
│  │  ┌──────┐ ┌──────┐ ┌──────┐ ┌────────┐ │  │
│  │  │Hello │ │ Hash │ │ Sort │ │ 看板    │ │  │
│  │  │ World│ │      │ │      │ │(图表)  │ │  │
│  │  └──────┘ └──────┘ └──────┘ └────────┘ │  │
│  │  [导出按钮]                               │  │
│  └───────────────────────────────────────────┘  │
└──────────────────┬──────────────────────────────┘
                   │ HTTP REST API
┌──────────────────▼──────────────────────────────┐
│                 后端 (Spring Boot)                │
│  ┌──────────────┐  ┌──────────────┐            │
│  │ API 控制器   │  │ 埋点拦截器   │            │
│  │ /api/hello   │  │ Tracking     │            │
│  │ /api/hash    │  │ Interceptor  │            │
│  │ /api/sort    │  └──────┬───────┘            │
│  │ /api/export  │         │                    │
│  │ /api/stats   │         ▼                    │
│  └──────────────┘  ┌──────────────┐            │
│                    │ 数据库/内存  │            │
│                    │ 埋点记录表   │            │
│                    └──────────────┘            │
└─────────────────────────────────────────────────┘
```

## 4. 后端接口设计

### 4.1 HelloWorld 接口

```
GET /api/hello
```

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "greeting": "Hello World! Welcome to Library System",
    "timestamp": "2025-01-13T10:00:00"
  }
}
```

### 4.2 哈希算法接口

```
POST /api/hash
Content-Type: application/json
```

**请求体**:
```json
{
  "input": "待哈希的字符串",
  "algorithm": "MD5|SHA-256|SHA-512"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "input": "待哈希的字符串",
    "algorithm": "SHA-256",
    "hashResult": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
  }
}
```

### 4.3 冒泡排序接口

```
POST /api/sort
Content-Type: application/json
```

**请求体**:
```json
{
  "numbers": [3, 1, 4, 1, 5, 9, 2, 6],
  "order": "asc|desc"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "originalArray": [3, 1, 4, 1, 5, 9, 2, 6],
    "sortedArray": [1, 1, 2, 3, 4, 5, 6, 9],
    "order": "asc",
    "swapCount": 12,
    "executionTimeMs": 0.5
  }
}
```

### 4.4 导出接口

```
GET /api/export?type=hello|hash|sort&format=json|csv
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 是 | 导出类型: hello/hash/sort |
| format | String | 否 | 导出格式: json(默认)/csv |

**响应**: 文件下载 (Content-Disposition: attachment)

### 4.5 统计接口

```
GET /api/stats/overview
```

获取总体统计数据，用于前端图表展示。

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "totalCalls": 1024,
    "byApi": {
      "hello": 350,
      "hash": 420,
      "sort": 254
    },
    "byDimension": {
      "personType": [
        {"label": "管理员", "value": 400},
        {"label": "普通用户", "value": 624}
      ],
      "personLevel": [
        {"label": "初级", "value": 300},
        {"label": "中级", "value": 500},
        {"label": "高级", "value": 224}
      ],
      "personDept": [
        {"label": "技术部", "value": 450},
        {"label": "运营部", "value": 300},
        {"label": "市场部", "value": 274}
      ]
    },
    "trend": [
      {"date": "2025-01-07", "count": 120},
      {"date": "2025-01-08", "count": 150},
      {"date": "2025-01-09", "count": 98},
      {"date": "2025-01-10", "count": 210},
      {"date": "2025-01-11", "count": 180},
      {"date": "2025-01-12", "count": 145},
      {"date": "2025-01-13", "count": 121}
    ]
  }
}
```

```
GET /api/stats/detail?page=1&size=20&dimension=type|level|dept
```

获取详细统计记录。

## 5. 埋点设计

### 5.1 埋点数据模型

使用 Spring Boot HandlerInterceptor 实现统一拦截，自动记录：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| apiName | String | 接口名称 (hello/hash/sort) |
| callerName | String | 调用人 (从请求头 X-Caller-Name 获取) |
| personType | String | 人员类型 (从请求头 X-Person-Type 获取) |
| personLevel | String | 人员层级 (从请求头 X-Person-Level 获取) |
| personDept | String | 人员部门 (从请求头 X-Person-Dept 获取) |
| callTime | LocalDateTime | 调用时间 |
| responseTimeMs | Long | 响应耗时(ms) |
| status | String | 状态 (success/error) |

### 5.2 拦截器实现

- 自定义 `@TrackedApi` 注解标记需要埋点的接口
- `TrackingInterceptor` 实现 `HandlerInterceptor`，在 preHandle 记录开始时间，afterCompletion 记录完整埋点信息
- 数据存储：H2/MySQL 的 `api_tracking_log` 表

## 6. 前端页面设计

### 6.1 路由与页面结构

```
路由: /dashboard
页面: ApiDashboard.vue
```

### 6.2 组件树

```
ApiDashboard.vue (主页面)
├── el-tabs (Tab 切换)
│   ├── el-tab-pane: Hello World
│   │   └── HelloWorldPanel.vue
│   ├── el-tab-pane: 哈希算法
│   │   └── HashPanel.vue
│   ├── el-tab-pane: 冒泡排序
│   │   └── SortPanel.vue
│   └── el-tab-pane: 调用统计看板
│       └── StatsDashboard.vue
│           ├── 维度筛选器 (人员类型/层级/部门)
│           ├── 折线图 (趋势)
│           ├── 饼图 (各维度分布)
│           └── 柱状图 (各接口调用量对比)
├── el-button: 导出按钮 (全局导出)
```

### 6.3 Tab 交互说明

| Tab | 说明 |
|-----|------|
| Hello World | 页面加载时自动调用 GET /api/hello，展示返回的 greeting 和时间戳 |
| 哈希算法 | 用户输入文本 + 选择算法 (MD5/SHA-256/SHA-512)，点击"计算"按钮调 POST /api/hash |
| 冒泡排序 | 用户输入逗号分隔数字 + 选择排序顺序 (asc/desc)，点击"排序"按钮调 POST /api/sort |
| 调用统计看板 | 展示三个筛选项（人员类型/层级/部门），三种图表（折线/饼图/柱状图） |

### 6.4 图表联动

- 折线图（左上）：展示近7天调用趋势
- 饼图（右上）：按当前选择的维度（类型/层级/部门）展示分布
- 柱状图（下方）：按三个 API 接口分组展示调用量对比
- 维度切换时，饼图和柱状图联动刷新

## 7. 导出功能

### 7.1 前端导出流程

1. 点击"导出"按钮
2. 弹出下拉选择：导出类型 (HelloWorld/Hash/Sort/全部)
3. 选择格式 (JSON/CSV)
4. 调用 GET /api/export?type=xxx&format=xxx
5. 浏览器下载文件

### 7.2 后端导出实现

- 使用 Spring 的 `StreamingResponseBody` 或 `ResponseEntity<Resource>`
- CSV 格式使用 OpenCSV 或手动构建
- 设置响应头 `Content-Disposition: attachment; filename=xxx`

## 8. 仓间接口对齐点

| 对齐点 | 前端 | 后端 |
|--------|------|------|
| API 基础路径 | 请求 BASE_URL + /api/* | 统一前缀 /api |
| 请求头埋点 | 每次请求携带 X-Caller-Name, X-Person-Type, X-Person-Level, X-Person-Dept | TrackingInterceptor 统一解析 |
| 统一响应格式 | 解析 {code, message, data} | 统一封装 ResponseEntity |
| 导出格式 | 支持 JSON/CSV 下载 | 根据 format 参数返回不同 Content-Type |
| 统计维度 | 传递 dimension 参数 | 按 dimension 分组聚合查询 |

## 9. 数据流

```
用户操作 → Vue Router → ApiDashboard.vue → Tab 组件
                                            ↓
                                    axios 调用后端 API
                                            ↓
                              TrackingInterceptor 记录埋点
                                            ↓
                               Controller 处理业务逻辑
                                            ↓
                              返回 JSON 响应 → 前端渲染
```

## 10. 数据存储

### 建表语句 (H2/MySQL)

```sql
CREATE TABLE api_tracking_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name VARCHAR(50) NOT NULL,
    caller_name VARCHAR(100),
    person_type VARCHAR(50),
    person_level VARCHAR(50),
    person_dept VARCHAR(100),
    call_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    response_time_ms BIGINT,
    status VARCHAR(20)
);
```

## 11. 未来扩展

- 支持更多维度的统计分析
- 导出支持 PDF/Excel 格式
- 实时 WebSocket 推送新调用数据
- 权限控制，不同角色看到不同维度的数据
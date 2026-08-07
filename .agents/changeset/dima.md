# 需求澄清与设计摘要 — hello world-1.0T2

> 阶段：需求澄清  
> 日期：2026-08-06  
> 状态：✅ 已确认

---

## 一、需求概述

在 library-backend 与 library-frontend 两个全新仓库中，实现以下功能：

1. **三个后端接口**：helloworld、哈希算法、冒泡排序
2. **前端展示页**：新增一个页面，包含三个 Tab 分别展示上述接口的执行结果
3. **导出功能**：每个 Tab 增加导出按钮，后端提供导出接口，支持导出当前 Tab 的展示结果
4. **调用埋点**：后端记录每次接口调用的次数与调用人信息
5. **可视化报表**：前端在同一页面内嵌报表区域，以折线图、饼图、柱状图展示调用情况，支持按人员类型、人员层级、人员部门等维度筛选

---

## 二、技术决策汇总

| 决策项 | 选定方案 | 备注 |
|---|---|---|
| 后端技术栈 | Java + Spring Boot | 企业级主流，生态成熟 |
| 前端技术栈 | React + TypeScript | 组件化好，ECharts 图表库丰富 |
| 数据存储 | H2 内存数据库 | 零部署，适合 demo；表结构兼容 MySQL/PG，后续可平滑迁移 |
| 导出格式 | Excel (.xlsx) | 企业通用，Apache POI / EasyExcel 均可生成 |
| 人员信息来源 | 请求参数模拟 | 前端传入 userId / userType / level / department，无需对接真实认证/HR 系统 |
| 报表时间范围 | 支持日期范围筛选 | 前端提供日期选择器，后端按 start/end 过滤 |

---

## 三、跨仓依赖与现状摘要

### 3.1 仓库现状

- **library-backend**：空仓（仅 `.git` + `README.md`），需从零初始化 Spring Boot 项目
- **library-frontend**：空仓（仅 `.git` + `README.md`），需从零初始化 React + TS 项目

### 3.2 仓间对齐点

| 对齐项 | 说明 |
|---|---|
| API 契约 | 后端先定义 OpenAPI/Swagger，前端按契约开发；三个业务接口 + 导出接口 + 报表查询接口共 5 个端点 |
| 埋点字段 | 统一约定：`userId`, `userType`, `level`, `department`, `apiName`, `timestamp` |
| 导出文件格式 | 后端返回 `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`，前端 Blob 下载 |
| 图表数据格式 | 后端返回聚合后的 JSON（维度 + 数值数组），前端 ECharts 消费 |

---

## 四、待规划事项（下一节点）

1. 后端项目脚手架搭建（Spring Boot + H2 + EasyExcel）
2. 前端项目脚手架搭建（Vite + React + TS + ECharts）
3. API 契约详细设计（路径、请求体、响应体）
4. 埋点表结构设计
5. 报表聚合查询接口设计
6. 前后端联调与测试策略

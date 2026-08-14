# 需求澄清 — Hello World 与哈希函数

## 1. 需求概述

在图书管理系统后端项目中，使用 **TypeScript (Node.js)** 实现两个功能模块：

1. **Hello World**：一个输出 "Hello, World!" 的简单入口。
2. **哈希函数**：同时实现两种哈希——
   - **标准哈希**：基于 Node.js 内置 `crypto` 模块的 SHA-256 哈希。
   - **自定义哈希**：手写一个经典哈希算法（如 FNV-1a）。

## 2. 已确认决策

| 决策项 | 结论 |
|--------|------|
| 编程语言 | TypeScript (Node.js) |
| 哈希算法 | 两者都要：标准 SHA-256 + 自定义哈希（FNV-1a） |
| 项目结构 | `src/` 目录 |

## 3. 待澄清 / 默认决策

以下问题未获得用户明确回答，采用合理默认值：

| 问题 | 默认决策 | 影响 |
|------|----------|------|
| 是否需要测试 | 暂不创建测试文件 | 后续可补充 |
| 包管理器 | npm | Node.js 默认包管理器 |
| 模块系统 | CommonJS（ts-node 兼容） | 简单直接 |

## 4. 技术方案草案

### 4.1 Hello World (`src/hello.ts`)

- 导出一个 `sayHello()` 函数，返回 `"Hello, World!"` 字符串
- 直接运行时打印到控制台

### 4.2 标准哈希 (`src/hash.ts`)

- 导出 `sha256Hash(input: string): string` 函数
- 使用 `crypto.createHash('sha256')` 计算输入字符串的 SHA-256 摘要
- 返回十六进制编码的哈希值

### 4.3 自定义哈希 (`src/customHash.ts`)

- 导出 `fnv1aHash(input: string): string` 函数
- 实现 FNV-1a 算法（Fowler–Noll–Vo 哈希），一种经典的非加密哈希函数
- 返回十六进制编码的哈希值

### 4.4 项目初始化

- 创建 `package.json`（含 `typescript`、`ts-node` 依赖）
- 创建 `tsconfig.json`（基础 TypeScript 配置）

## 5. 影响范围

- **新增文件**：`src/hello.ts`、`src/hash.ts`、`src/customHash.ts`、`package.json`、`tsconfig.json`
- **修改文件**：无（现有 `README.md` 保持不变）
- **破坏性变更**：无

## 6. 风险与注意事项

- 项目当前为空仓库，需从零初始化 TypeScript 项目结构
- SHA-256 为单向哈希，不可逆；如需密码存储场景，后续可升级为 bcrypt/argon2
- FNV-1a 为非加密哈希，速度快但不适用于安全场景（如密码存储），适合哈希表、校验和等用途

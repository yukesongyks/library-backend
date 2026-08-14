# Hello World 与哈希函数 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在空的 TypeScript/Node.js 项目中实现 Hello World 输出模块和两种哈希函数（SHA-256 + FNV-1a）。

**Architecture:** 项目采用 CommonJS 模块系统，使用 TypeScript 编写，通过 ts-node 直接运行。三个功能模块各自独立：`hello.ts` 负责问候输出，`hash.ts` 封装 Node.js 内置 crypto 的 SHA-256，`customHash.ts` 手写 FNV-1a 算法。每个模块导出一个纯函数，直接运行时打印示例输出。

**Tech Stack:** TypeScript 5.x, Node.js (>=16), ts-node, npm

---

## Global Constraints

- 语言：TypeScript，目标编译为 CommonJS
- 运行时：Node.js >= 16（内置 crypto 模块支持 SHA-256）
- 模块系统：CommonJS（`"module": "commonjs"` in tsconfig）
- 代码目录：所有源码放在 `src/` 下
- 不引入第三方哈希库，SHA-256 使用 Node.js 内置 `crypto`
- 哈希函数返回值为小写十六进制字符串
- 暂不创建测试文件，通过 `npx ts-node` 直接运行验证

---

## File Structure

| 文件路径 | 职责 | 操作 |
|----------|------|------|
| `package.json` | 项目元数据、依赖声明（typescript, ts-node, @types/node） | 新建 |
| `tsconfig.json` | TypeScript 编译配置 | 新建 |
| `src/hello.ts` | 导出 `sayHello()` 函数，返回并打印 "Hello, World!" | 新建 |
| `src/hash.ts` | 导出 `sha256Hash(input: string): string`，基于 crypto 的 SHA-256 | 新建 |
| `src/customHash.ts` | 导出 `fnv1aHash(input: string): string`，手写 FNV-1a 算法 | 新建 |

---

## Task 1: 项目初始化（package.json + tsconfig.json）

**Files:**
- Create: `package.json`
- Create: `tsconfig.json`

**Interfaces:**
- Consumes: 无（基础项目脚手架）
- Produces: 可用的 TypeScript 编译环境和 ts-node 运行能力

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "hello-hash",
  "version": "1.0.0",
  "description": "Hello World and Hash Functions",
  "main": "src/hello.ts",
  "scripts": {
    "start:hello": "ts-node src/hello.ts",
    "start:hash": "ts-node src/hash.ts",
    "start:custom": "ts-node src/customHash.ts"
  },
  "devDependencies": {
    "typescript": "^5.3.3",
    "ts-node": "^10.9.2",
    "@types/node": "^20.11.0"
  }
}
```

- [ ] **Step 2: 创建 tsconfig.json**

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "commonjs",
    "strict": true,
    "esModuleInterop": true,
    "outDir": "./dist",
    "rootDir": "./src",
    "declaration": true,
    "sourceMap": true
  },
  "include": ["src/**/*"]
}
```

- [ ] **Step 3: 安装依赖**

Run: `npm install`
Expected: 生成 `node_modules/` 和 `package-lock.json`，无报错退出

- [ ] **Step 4: 验证 TypeScript 可用**

Run: `npx tsc --version`
Expected: 输出 `Version 5.x.x`（具体小版本号可能不同）

- [ ] **Step 5: Commit**

```bash
git add package.json tsconfig.json
git commit -m "chore: initialize TypeScript project with ts-node"
```

---

## Task 2: Hello World 模块（src/hello.ts）

**Files:**
- Create: `src/hello.ts`

**Interfaces:**
- Consumes: 无
- Produces: `sayHello(): string` — 返回 `"Hello, World!"` 并打印到控制台

- [ ] **Step 1: 创建 src/hello.ts**

```typescript
/**
 * 返回 "Hello, World!" 字符串
 */
export function sayHello(): string {
  return "Hello, World!";
}

// 直接运行时打印到控制台
if (require.main === module) {
  console.log(sayHello());
}
```

- [ ] **Step 2: 运行验证**

Run: `npx ts-node src/hello.ts`
Expected output:
```
Hello, World!
```

- [ ] **Step 3: Commit**

```bash
git add src/hello.ts
git commit -m "feat: add Hello World module"
```

---

## Task 3: SHA-256 标准哈希模块（src/hash.ts）

**Files:**
- Create: `src/hash.ts`

**Interfaces:**
- Consumes: Node.js 内置 `crypto` 模块
- Produces: `sha256Hash(input: string): string` — 返回输入字符串的 SHA-256 十六进制摘要

- [ ] **Step 1: 创建 src/hash.ts**

```typescript
import { createHash } from "crypto";

/**
 * 计算输入字符串的 SHA-256 哈希值
 * @param input - 待哈希的字符串
 * @returns 小写十六进制编码的 SHA-256 摘要
 */
export function sha256Hash(input: string): string {
  return createHash("sha256").update(input, "utf-8").digest("hex");
}

// 直接运行时打印示例
if (require.main === module) {
  const testInput = "Hello, World!";
  console.log(`Input:   ${testInput}`);
  console.log(`SHA-256: ${sha256Hash(testInput)}`);
}
```

- [ ] **Step 2: 运行验证**

Run: `npx ts-node src/hash.ts`
Expected output:
```
Input:   Hello, World!
SHA-256: dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f
```

> 验证要点：SHA-256("Hello, World!") 的已知正确值为 `dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f`，输出必须与此一致。

- [ ] **Step 3: Commit**

```bash
git add src/hash.ts
git commit -m "feat: add SHA-256 hash module using crypto"
```

---

## Task 4: FNV-1a 自定义哈希模块（src/customHash.ts）

**Files:**
- Create: `src/customHash.ts`

**Interfaces:**
- Consumes: 无外部依赖（纯算法实现）
- Produces: `fnv1aHash(input: string): string` — 返回输入字符串的 FNV-1a 32 位十六进制哈希

- [ ] **Step 1: 创建 src/customHash.ts**

```typescript
// FNV-1a 32-bit 常量
const FNV_OFFSET_BASIS = 0x811c9dc5;
const FNV_PRIME = 0x01000193;

/**
 * 计算输入字符串的 FNV-1a 32 位哈希值
 * FNV-1a (Fowler–Noll–Vo) 是一种经典的非加密哈希函数，
 * 速度快、分布均匀，适合哈希表和校验和场景。
 *
 * @param input - 待哈希的字符串
 * @returns 小写十六进制编码的 32 位哈希值（8 个字符）
 */
export function fnv1aHash(input: string): string {
  let hash = FNV_OFFSET_BASIS;

  for (let i = 0; i < input.length; i++) {
    // XOR with the byte value of the character
    hash ^= input.charCodeAt(i);
    // Multiply by FNV prime, keep within 32-bit unsigned range
    hash = Math.imul(hash, FNV_PRIME) >>> 0;
  }

  // 转为 8 位十六进制字符串（补零）
  return hash.toString(16).padStart(8, "0");
}

// 直接运行时打印示例
if (require.main === module) {
  const testInput = "Hello, World!";
  console.log(`Input:  ${testInput}`);
  console.log(`FNV-1a: ${fnv1aHash(testInput)}`);
}
```

- [ ] **Step 2: 运行验证**

Run: `npx ts-node src/customHash.ts`
Expected output:
```
Input:  Hello, World!
FNV-1a: 10450482
```

> 验证要点：FNV-1a 32-bit("Hello, World!") 的已知正确值为 `10450482`（十六进制），输出必须与此一致。

- [ ] **Step 3: Commit**

```bash
git add src/customHash.ts
git commit -m "feat: add FNV-1a custom hash module"
```

---

## Task 5: 全量编译验证

**Files:**
- 无新增文件（验证已有产物）

**Interfaces:**
- Consumes: Task 1–4 的全部产物
- Produces: 编译通过的 `dist/` 目录

- [ ] **Step 1: TypeScript 全量编译**

Run: `npx tsc`
Expected: 无错误输出，生成 `dist/` 目录含 `hello.js`、`hash.js`、`customHash.js` 及对应 `.d.ts` 和 `.js.map` 文件

- [ ] **Step 2: 验证编译产物存在**

Run: `ls dist/`
Expected output 包含:
```
customHash.d.ts  customHash.js  customHash.js.map
hash.d.ts  hash.js  hash.js.map
hello.d.ts  hello.js  hello.js.map
```

- [ ] **Step 3: 通过 Node.js 直接运行编译产物验证**

Run: `node dist/hello.js && node dist/hash.js && node dist/customHash.js`
Expected output:
```
Hello, World!
Input:   Hello, World!
SHA-256: dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f
Input:  Hello, World!
FNV-1a: 10450482
```

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "chore: verify full build and add compiled output"
```

---

## Self-Review Checklist

| 检查项 | 状态 |
|--------|------|
| 需求覆盖：Hello World 模块 | ✅ Task 2 |
| 需求覆盖：SHA-256 标准哈希 | ✅ Task 3 |
| 需求覆盖：FNV-1a 自定义哈希 | ✅ Task 4 |
| 需求覆盖：项目初始化 | ✅ Task 1 |
| 无占位符（TBD/TODO/implement later） | ✅ 所有步骤含完整代码 |
| 类型一致性：`sayHello(): string` | ✅ Task 2 定义，无其他引用冲突 |
| 类型一致性：`sha256Hash(input: string): string` | ✅ Task 3 定义，签名一致 |
| 类型一致性：`fnv1aHash(input: string): string` | ✅ Task 4 定义，签名一致 |
| 精确文件路径 | ✅ 所有路径已明确 |
| 精确运行命令与预期输出 | ✅ 每个验证步骤含命令和期望输出 |

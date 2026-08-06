# 图书管理系统设计规格说明书 (Spec)

> **版本**: 1.0  
> **日期**: 2026-08-06  
> **状态**: 已澄清，待审阅  
> **仓库**: library-frontend / library-backend  

---

## 1. 概述

### 1.1 项目背景

构建一个图书管理系统，采用前后端分离架构，双仓库协作开发。前后端均为全新项目（greenfield），无既有代码与接口契约遗留。

### 1.2 项目目标

提供图书信息管理的基础能力，支持管理员对图书进行增删改查，支持读者浏览图书目录。系统以最小可用集（MVP）为起点，遵循 YAGNI 原则，为后续迭代预留向后兼容扩展空间。

### 1.3 范围定义

| 范围 | 说明 |
|------|------|
| ✅ 纳入 | 图书 CRUD、双角色鉴权（管理员/读者）、JWT 认证 |
| ❌ 排除 | 借阅归还、读者档案管理、搜索、报表统计、逾期通知、预约、罚款、评论 |

> 排除项均为后续迭代候选，按契约优先原则以新增字段/新增接口方式向后兼容扩展。

---

## 2. 功能需求

### 2.1 角色定义

| 角色 | 标识 | 说明 |
|------|------|------|
| 管理员 | `ROLE_ADMIN` | 图书完整 CRUD 权限 |
| 读者 | `ROLE_READER` | 仅查看图书列表与详情 |

### 2.2 功能用例

| 用例 | 角色 | 描述 |
|------|------|------|
| UC-1 登录 | 管理员/读者 | 用户名+密码登录，获取 JWT |
| UC-2 查看图书列表 | 管理员/读者 | 分页查看图书列表 |
| UC-3 查看图书详情 | 管理员/读者 | 查看单本图书完整信息 |
| UC-4 新增图书 | 管理员 | 录入新书信息 |
| UC-5 修改图书 | 管理员 | 修改已有图书信息 |
| UC-6 删除图书 | 管理员 | 删除指定图书 |

### 2.3 权限矩阵

| 资源/操作 | 列表 (GET) | 详情 (GET) | 新增 (POST) | 修改 (PUT) | 删除 (DELETE) |
|-----------|:---------:|:---------:|:----------:|:----------:|:------------:|
| /api/books | 管理员+读者 | 管理员+读者 | 管理员 | 管理员 | 管理员 |
| /api/auth/login | — | — | 所有人 | — | — |

---

## 3. 技术架构

### 3.1 技术栈

| 仓库 | 技术栈 | 版本 |
|------|--------|------|
| library-frontend | React + Vite + TypeScript | React 18 / Vite 5 |
| library-backend | Spring Boot + Java + Spring Security + JWT | Spring Boot 3 / Java 17 |
| 数据库 | MySQL | 8.x |
| 数据库迁移 | Flyway | — |

### 3.2 部署拓扑

```
┌─────────────────┐     HTTP/JSON      ┌──────────────────────┐
│  library-frontend│ ───────────────▶  │  library-backend     │
│  (React + Vite)  │  /api/* (proxy)   │  (Spring Boot REST)  │
│  Port: 5173      │                   │  Port: 8080          │
└─────────────────┘                    └────────┬─────────────┘
                                                │ JDBC
                                       ┌────────▼─────────┐
                                       │  MySQL 8          │
                                       │  Port: 3306       │
                                       └──────────────────┘
```

### 3.3 前后端通信约定

- **协议**: RESTful HTTP + JSON
- **前端代理**: Vite dev server proxy `/api` → `http://localhost:8080`
- **契约定义方**: library-backend（后端定义 DTO 与接口，前端消费）
- **字符编码**: UTF-8
- **时间格式**: ISO 8601（`yyyy-MM-dd'T'HH:mm:ss'Z'`），UTC

---

## 4. 数据模型

### 4.1 ER 关系

```
┌──────────────┐         ┌──────────────────┐
│     User     │         │      Book       │
├──────────────┤         ├──────────────────┤
│ id (PK)      │         │ id (PK)          │
│ username     │         │ title            │
│ password     │         │ author           │
│ role         │         │ isbn             │
└──────────────┘         │ publisher        │
                         │ category         │
                         │ stock            │
                         │ total_stock      │
                         └──────────────────┘
```

> User 与 Book 本期无直接关联关系（无借阅流程）。User 表仅为支撑双角色 JWT 鉴权。

### 4.2 表结构 DDL

#### 4.2.1 `book` 表

```sql
CREATE TABLE `book` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(200) NOT NULL COMMENT '书名',
    `author`      VARCHAR(100) NOT NULL COMMENT '作者',
    `isbn`        VARCHAR(20)  NOT NULL COMMENT 'ISBN',
    `publisher`   VARCHAR(100)          COMMENT '出版社',
    `category`    VARCHAR(50)           COMMENT '分类',
    `stock`       INT          NOT NULL DEFAULT 0  COMMENT '当前库存',
    `total_stock` INT          NOT NULL DEFAULT 0  COMMENT '总库存',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_isbn` (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';
```

#### 4.2.2 `user` 表

```sql
CREATE TABLE `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `username`   VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`   VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
    `role`       VARCHAR(20)  NOT NULL COMMENT '角色: ROLE_ADMIN / ROLE_READER',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 4.3 Flyway 迁移脚本

| 脚本 | 路径 | 内容 |
|------|------|------|
| V1__create_tables.sql | `src/main/resources/db/migration/` | 创建 book + user 表 |
| V2__seed_admin_user.sql | `src/main/resources/db/migration/` | 初始化管理员账号 |

---

## 5. API 契约

### 5.1 通用约定

#### 请求头

```
Content-Type: application/json
Authorization: Bearer <JWT>   (除 /api/auth/login 外均需携带)
```

#### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

#### 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

#### 错误响应

```json
{
  "code": 400,
  "message": "Validation failed: title must not be blank",
  "data": null
}
```

#### HTTP 状态码

| 状态码 | 含义 |
|--------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 5.2 端点详表

#### 5.2.1 登录

```
POST /api/auth/login
Auth: 无需
```

**请求体:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应体 (200):**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "role": "ROLE_ADMIN",
    "username": "admin"
  }
}
```

**错误 (401):**
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

#### 5.2.2 图书列表

```
GET /api/books?page=0&size=10
Auth: ROLE_ADMIN, ROLE_READER
```

**响应体 (200):**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "三体",
        "author": "刘慈欣",
        "isbn": "9787536692930",
        "publisher": "重庆出版社",
        "category": "科幻",
        "stock": 5,
        "totalStock": 10,
        "createdAt": "2026-08-06T10:00:00Z",
        "updatedAt": "2026-08-06T10:00:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### 5.2.3 图书详情

```
GET /api/books/{id}
Auth: ROLE_ADMIN, ROLE_READER
```

**响应体 (200):** 同列表项单个对象

**错误 (404):** `"图书不存在"`

#### 5.2.4 新增图书

```
POST /api/books
Auth: ROLE_ADMIN
```

**请求体:**
```json
{
  "title": "三体",
  "author": "刘慈欣",
  "isbn": "9787536692930",
  "publisher": "重庆出版社",
  "category": "科幻",
  "totalStock": 10
}
```

> `stock` 初始值等于 `totalStock`，由后端自动设置。

**响应体 (201):** 返回创建后的图书对象

**校验规则:**

| 字段 | 规则 |
|------|------|
| title | 必填，1-200 字符 |
| author | 必填，1-100 字符 |
| isbn | 必填，唯一，10-20 字符 |
| publisher | 可选，≤100 字符 |
| category | 可选，≤50 字符 |
| totalStock | 必填，≥0 |

#### 5.2.5 修改图书

```
PUT /api/books/{id}
Auth: ROLE_ADMIN
```

**请求体:** 同新增（所有字段均可修改）

**响应体 (200):** 返回修改后的图书对象

#### 5.2.6 删除图书

```
DELETE /api/books/{id}
Auth: ROLE_ADMIN
```

**响应体 (200):**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 6. 模块设计

### 6.1 后端模块拆分 (library-backend)

```
src/main/java/com/library/
├── LibraryBackendApplication.java        # 启动类
├── config/
│   ├── SecurityConfig.java               # Spring Security 配置
│   └── CorsConfig.java                   # 跨域配置
├── controller/
│   ├── AuthController.java               # 登录接口
│   └── BookController.java               # 图书 CRUD 接口
├── service/
│   ├── AuthService.java                  # 认证逻辑
│   └── BookService.java                  # 图书业务逻辑
├── repository/
│   ├── BookRepository.java               # Book JPA Repository
│   └── UserRepository.java               # User JPA Repository
├── entity/
│   ├── Book.java                         # Book 实体
│   └── User.java                         # User 实体
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   └── BookRequest.java              # 新增/修改图书共用
│   └── response/
│       ├── LoginResponse.java
│       ├── BookResponse.java
│       └── ApiResponse.java              # 统一响应包装
├── security/
│   ├── JwtTokenProvider.java             # JWT 生成与验证
│   ├── JwtAuthenticationFilter.java      # JWT 请求过滤器
│   └── CustomUserDetailsService.java     # 用户详情加载
└── exception/
    ├── GlobalExceptionHandler.java       # 全局异常处理
    └── BusinessException.java            # 业务异常
```

### 6.2 前端模块拆分 (library-frontend)

```
src/
├── main.tsx                              # 应用入口
├── App.tsx                               # 路由定义
├── api/
│   ├── client.ts                         # Axios 实例（附带 JWT）
│   └── books.ts                          # 图书 API 调用
├── pages/
│   ├── Login.tsx                         # 登录页
│   ├── BookList.tsx                      # 图书列表页
│   └── BookForm.tsx                      # 图书新增/编辑表单
├── components/
│   └── BookTable.tsx                     # 图书表格组件
├── context/
│   └── AuthContext.tsx                   # 认证状态管理
└── types/
    └── book.ts                           # Book TypeScript 类型定义
```

### 6.3 前端路由

| 路径 | 组件 | 权限 |
|------|------|------|
| `/login` | Login | 公开 |
| `/books` | BookList | 管理员+读者 |
| `/books/new` | BookForm | 管理员 |
| `/books/:id/edit` | BookForm | 管理员 |

---

## 7. 安全设计

### 7.1 JWT 认证流程

```
1. 用户 POST /api/auth/login（username + password）
2. 后端验证凭据 → JwtTokenProvider 生成 JWT（含 username + role）
3. 返回 JWT token
4. 前端存储 token（localStorage）
5. 后续请求附带 Authorization: Bearer <token>
6. JwtAuthenticationFilter 拦截 → 验证 → 设置 SecurityContext
7. @PreAuthorize 注解校验角色权限
```

### 7.2 密码安全

- **加密算法**: BCrypt（Spring Security `PasswordEncoder`）
- **初始管理员**: Flyway V2 种子脚本创建（密码 BCrypt 预加密）

### 7.3 接口权限

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'READER')")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> list(...) { }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'READER')")
    public ResponseEntity<ApiResponse<BookResponse>> getById(...) { }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> create(...) { }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> update(...) { }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(...) { }
}
```

### 7.4 CORS 配置

- 允许来源: `http://localhost:5173`（前端 dev server）
- 允许方法: GET, POST, PUT, DELETE
- 允许头: Authorization, Content-Type

---

## 8. 前后端类型对齐

### 8.1 Book 类型映射

| 后端字段 (Java camelCase) | JSON 字段 | 前端类型 (TS) |
|--------------------------|-----------|--------------|
| id (Long) | id | number |
| title (String) | title | string |
| author (String) | author | string |
| isbn (String) | isbn | string |
| publisher (String) | publisher | string \| null |
| category (String) | category | string \| null |
| stock (Integer) | stock | number |
| totalStock (Integer) | totalStock | number |
| createdAt (LocalDateTime) | createdAt | string (ISO 8601) |
| updatedAt (LocalDateTime) | updatedAt | string (ISO 8601) |

### 8.2 前端 TypeScript 类型定义

```typescript
// src/types/book.ts
export interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  publisher: string | null;
  category: string | null;
  stock: number;
  totalStock: number;
  createdAt: string;
  updatedAt: string;
}

export interface BookRequest {
  title: string;
  author: string;
  isbn: string;
  publisher?: string;
  category?: string;
  totalStock: number;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface LoginResponse {
  token: string;
  role: string;
  username: string;
}
```

---

## 9. 非功能需求

| 维度 | 要求 |
|------|------|
| 响应时间 | 单次 API 请求 < 500ms（不含网络） |
| 分页 | 默认 page=0, size=10，最大 size=100 |
| 并发 | 支持基础并发，无特殊高并发要求 |
| 日志 | SLF4J + Logback，记录请求/错误 |
| 异常处理 | 全局异常处理器统一返回 ApiResponse |

---

## 10. 验收标准

### 10.1 后端验收

- [ ] `POST /api/auth/login` 返回有效 JWT
- [ ] 无 token / 无效 token 请求受保护接口返回 401
- [ ] 读者角色请求 POST/PUT/DELETE 返回 403
- [ ] 图书 CRUD 全流程通过（新增→查询→修改→删除）
- [ ] ISBN 重复新增返回 400
- [ ] 分页参数正确返回
- [ ] Flyway 迁移脚本执行成功

### 10.2 前端验收

- [ ] 登录页可登录并获取 token
- [ ] token 存储于 localStorage 并附带于后续请求
- [ ] 图书列表页分页展示
- [ ] 管理员可见新增/编辑/删除按钮，读者不可见
- [ ] 读者访问管理路由被重定向至 /books
- [ ] 表单校验提示正确

### 10.3 跨库对齐验收

- [ ] 前端 TypeScript 类型与后端 DTO 字段一一对应
- [ ] 统一响应格式 `{ code, message, data }` 前后端一致
- [ ] 错误码与 HTTP 状态码前后端约定一致
- [ ] JWT token 前后端解析一致

---

## 11. 向后兼容扩展预留

后续迭代可按契约优先原则**新增**（不破坏既有契约）：

| 扩展方向 | 兼容方式 |
|----------|----------|
| 借阅归还 | 新增 `borrow_record` 表 + `/api/borrows` 接口，Book 表新增 `borrowed_count` 字段 |
| 读者管理 | 新增 `reader` 表 + `/api/readers` 接口 |
| 搜索 | 新增 `GET /api/books/search?q=` 接口，不改动既有列表接口 |
| 分类管理 | 新增 `category` 表 + `/api/categories` 接口 |

---

## 12. 决策记录

| # | 决策点 | 选定方案 | 决策来源 |
|---|--------|----------|----------|
| 1 | 技术栈 | React + Spring Boot | 用户确认 |
| 2 | 功能范围 | 仅图书 CRUD（YAGNI 最小集） | 用户确认 |
| 3 | 鉴权 | 双角色 + JWT（管理员 CRUD / 读者只读） | 用户确认 |
| 4 | 数据存储 | MySQL 8 持久化 + Flyway | 用户确认 |
| 5 | 接口契约 | RESTful + JSON，后端定义 | 自主接管 |
| 6 | 实体模型 | Book + User（仅支撑鉴权） | 自主接管 |

---

*本 Spec 基于 brainstorming 技能方法论产出，经需求澄清后固化。*

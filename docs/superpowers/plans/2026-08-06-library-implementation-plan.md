# 图书管理系统实施计划 (Implementation Plan)

> **版本**: 1.0
> **日期**: 2026-08-06
> **状态**: 待执行
> **关联 Spec**: `docs/superpowers/specs/2026-08-06-library-system-design.md`
> **仓库**: library-frontend / library-backend

---

## 1. 目标 (Goal)

依据系分设计规格说明书，从零搭建前后端分离的图书管理系统 MVP。后端提供 JWT 认证 + 图书 CRUD REST API，前端提供登录页与图书管理界面，双仓库通过 `/api` 代理对接。

**验收标准**:
- [ ] 后端启动无报错，Flyway 自动建表 + 种子数据
- [ ] `POST /api/auth/login` 返回 JWT，错误凭据返回 401
- [ ] `GET /api/books?page=0&size=10` 返回分页图书列表（需携带 JWT）
- [ ] `GET /api/books/{id}` 返回单本图书，不存在时返回 404
- [ ] `POST /api/books` 创建图书，ISBN 重复返回 400，无权限返回 403
- [ ] `PUT /api/books/{id}` 修改图书，返回更新后对象
- [ ] `DELETE /api/books/{id}` 删除图书，返回 `data: null`
- [ ] 前端登录页可获取并存储 JWT
- [ ] 前端图书列表页分页展示、新增/编辑/删除操作可联通后端
- [ ] 读者角色无法访问新增/编辑/删除（前端隐藏入口 + 后端 403 拦截）

---

## 2. 架构与技术栈 (Architecture & Tech Stack)

| 仓库 | 技术栈 | 版本 |
|------|--------|------|
| library-backend | Spring Boot + Java + Spring Security + JWT + JPA + Flyway | Spring Boot 3.x / Java 17 |
| library-frontend | React + Vite + TypeScript + Axios + React Router | React 18 / Vite 5 |
| 数据库 | MySQL | 8.x |

```
library-frontend (Port 5173) ──/api proxy──▶ library-backend (Port 8080) ──JDBC──▶ MySQL 8 (Port 3306)
```

- **契约定义方**: library-backend
- **通信协议**: RESTful HTTP + JSON
- **字符编码**: UTF-8
- **时间格式**: ISO 8601 UTC (`yyyy-MM-dd'T'HH:mm:ss'Z'`)

---

## 3. 文件结构 (File Structure)

### 3.1 library-backend

```
library-backend-main/
├── pom.xml
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
│       ├── V1__create_tables.sql
│       └── V2__seed_admin_user.sql
└── src/main/java/com/library/
    ├── LibraryBackendApplication.java
    ├── config/
    │   ├── SecurityConfig.java
    │   └── CorsConfig.java
    ├── controller/
    │   ├── AuthController.java
    │   └── BookController.java
    ├── service/
    │   ├── AuthService.java
    │   └── BookService.java
    ├── repository/
    │   ├── BookRepository.java
    │   └── UserRepository.java
    ├── entity/
    │   ├── Book.java
    │   └── User.java
    ├── dto/
    │   ├── request/
    │   │   ├── LoginRequest.java
    │   │   └── BookRequest.java
    │   └── response/
    │       ├── LoginResponse.java
    │       ├── BookResponse.java
    │       └── ApiResponse.java
    ├── security/
    │   ├── JwtTokenProvider.java
    │   ├── JwtAuthenticationFilter.java
    │   └── CustomUserDetailsService.java
    └── exception/
        ├── GlobalExceptionHandler.java
        └── BusinessException.java
```

### 3.2 library-frontend

```
library-frontend-main/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── index.html
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── api/
    │   ├── client.ts
    │   └── books.ts
    ├── pages/
    │   ├── Login.tsx
    │   ├── BookList.tsx
    │   └── BookForm.tsx
    ├── components/
    │   └── BookTable.tsx
    ├── context/
    │   └── AuthContext.tsx
    └── types/
        └── book.ts
```

---

## 4. 任务分解 (Task Breakdown)

### Phase 1: 后端项目骨架与数据层 (library-backend)

#### Task 1.1 — 初始化 Maven 项目骨架
- [ ] 创建 `pom.xml`：Spring Boot 3.x parent，Java 17，依赖 `spring-boot-starter-web` / `spring-boot-starter-data-jpa` / `spring-boot-starter-security` / `spring-boot-starter-validation` / `flyway-core` / `flyway-mysql` / `mysql-connector-j` / `jjwt-api`+`jjwt-impl`+`jjwt-jackson`(runtime)
- [ ] 创建 `src/main/resources/application.yml`：配置 datasource (MySQL 8, `jdbc:mysql://localhost:3306/library_db`)、JPA (`ddl-auto: validate`, `show-sql: true`)、Flyway (`enabled: true`, `locations: classpath:db/migration`)、自定义 JWT secret 与过期时间
- [ ] 创建 `src/main/java/com/library/LibraryBackendApplication.java`：`@SpringBootApplication` 启动类
- [ ] 验证：`mvn compile` 成功

#### Task 1.2 — Flyway 数据库迁移脚本
- [ ] 创建 `src/main/resources/db/migration/V1__create_tables.sql`：按 Spec DDL 创建 `book` 表（含 `uk_isbn` 唯一索引）和 `user` 表（含 `uk_username` 唯一索引）
- [ ] 创建 `src/main/resources/db/migration/V2__seed_admin_user.sql`：插入管理员种子数据（`admin` / BCrypt 加密 `admin123` / `ROLE_ADMIN`）
- [ ] 验证：启动后端，Flyway 日志显示 V1/V2 执行成功

#### Task 1.3 — JPA 实体层
- [ ] 创建 `Book.java`：`@Entity`，字段映射 `id`(`@Id`+`@GeneratedValue` IDENTITY)、`title`、`author`、`isbn`、`publisher`、`category`、`stock`、`totalStock`、`createdAt`(`@Column(name="created_at")`+`@CreationTimestamp`或`updatable=false`)、`updatedAt`(`@Column(name="updated_at")`+`@UpdateTimestamp`)
- [ ] 创建 `User.java`：`@Entity`，字段映射 `id`、`username`、`password`、`role`、`createdAt`、`updatedAt`
- [ ] 验证：`mvn compile` 成功，字段名与 DDL 列名通过 JPA 命名策略或 `@Column` 对齐

#### Task 1.4 — Repository 层
- [ ] 创建 `BookRepository.java`：`extends JpaRepository<Book, Long>`，新增 `boolean existsByIsbn(String isbn)` 和 `boolean existsByIsbnAndIdNot(String isbn, Long id)` 方法
- [ ] 创建 `UserRepository.java`：`extends JpaRepository<User, Long>`，新增 `Optional<User> findByUsername(String username)` 方法
- [ ] 验证：`mvn compile` 成功

---

### Phase 2: 后端安全与认证层 (library-backend)

#### Task 2.1 — DTO 层（安全与业务共用）
- [ ] 创建 `ApiResponse.java`：泛型包装 `<T>`，字段 `code`/`message`/`data`，提供 `success(T data)` / `success(int code, T data)` / `error(int code, String message)` 静态工厂
- [ ] 创建 `LoginRequest.java`：字段 `username`(`@NotBlank`)、`password`(`@NotBlank`)，Lombok `@Getter`/`@Setter`
- [ ] 创建 `LoginResponse.java`：字段 `token`/`role`/`username`
- [ ] 创建 `BookRequest.java`：字段 `title`(`@NotBlank`+`@Size(max=200)`)、`author`(`@NotBlank`+`@Size(max=100)`)、`isbn`(`@NotBlank`+`@Size(min=10,max=20)`)、`publisher`(`@Size(max=100)`)、`category`(`@Size(max=50)`)、`totalStock`(`@NotNull`+`@Min(0)`)
- [ ] 创建 `BookResponse.java`：字段 `id`/`title`/`author`/`isbn`/`publisher`/`category`/`stock`/`totalStock`/`createdAt`/`updatedAt`，通过 `@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")` 格式化时间
- [ ] 验证：`mvn compile` 成功

#### Task 2.2 — JWT 组件
- [ ] 创建 `JwtTokenProvider.java`：使用 `io.jsonwebtoken` (JJWT)，方法 `generateToken(String username, String role)`（签发 JWT，claims 含 `username`+`role`）、`validateToken(String token)`（解析验签）、`getUsernameFromToken(String token)`、`getRoleFromToken(String token)`；密钥与过期时间从 `application.yml` 读取
- [ ] 创建 `CustomUserDetailsService.java`：`implements UserDetailsService`，通过 `UserRepository.findByUsername()` 加载用户，构建 Spring Security `User`（username, password, `GrantedAuthority` = role）
- [ ] 创建 `JwtAuthenticationFilter.java`：`extends OncePerRequestFilter`，从 `Authorization: Bearer <token>` 提取 token → `JwtTokenProvider.validateToken` → 构建 `UsernamePasswordAuthenticationToken` → 存入 `SecurityContextHolder`
- [ ] 验证：`mvn compile` 成功

#### Task 2.3 — Spring Security 与 CORS 配置
- [ ] 创建 `SecurityConfig.java`：`@EnableMethodSecurity`，`SecurityFilterChain` Bean 配置：`/api/auth/login` 放行、其余 `authenticated()`、添加 `JwtAuthenticationFilter`（before `UsernamePasswordAuthenticationFilter`）、`PasswordEncoder` Bean（`BCryptPasswordEncoder`）、`AuthenticationManager` Bean
- [ ] 创建 `CorsConfig.java`：允许前端来源 `http://localhost:5173`，允许方法 GET/POST/PUT/DELETE，允许头 `Authorization`/`Content-Type`
- [ ] 验证：`mvn compile` 成功，启动后 `POST /api/auth/login` 放行

#### Task 2.4 — 认证 Controller 与 Service
- [ ] 创建 `AuthService.java`：方法 `login(LoginRequest)` → `AuthenticationManager.authenticate()` → 生成 JWT → 返回 `LoginResponse`
- [ ] 创建 `AuthController.java`：`@RestController` + `@RequestMapping("/api/auth")`，`POST /login` 端点，调用 `AuthService.login()`，返回 `ApiResponse<LoginResponse>`
- [ ] 验证：启动后端，`POST /api/auth/login` (admin/admin123) 返回 200 + token；错误密码返回 401

---

### Phase 3: 后端业务层 (library-backend)

#### Task 3.1 — 异常处理
- [ ] 创建 `BusinessException.java`：`extends RuntimeException`，持有 `code`（HTTP 状态码）和 `message`
- [ ] 创建 `GlobalExceptionHandler.java`：`@RestControllerAdvice`，处理 `BusinessException`（返回对应 code）、`MethodArgumentNotValidException`（400，拼接校验错误信息）、`AccessDeniedException`（403）、`Exception`（500 兜底），统一包装为 `ApiResponse`
- [ ] 验证：`mvn compile` 成功

#### Task 3.2 — 图书 Service
- [ ] 创建 `BookService.java`：
  - `list(int page, int size)` → `PageRequest.of(page, size)` → `bookRepository.findAll()` → 转换为 `Page<BookResponse>`
  - `getById(Long id)` → `findById` → 不存在抛 `BusinessException(404, "图书不存在")`
  - `create(BookRequest)` → 校验 ISBN 唯一（`existsByIsbn`，重复抛 400）→ 构建 `Book`（`stock = totalStock`）→ `save` → 转 `BookResponse`
  - `update(Long id, BookRequest)` → 查找 → 校验 ISBN 唯一排除自身（`existsByIsbnAndIdNot`）→ 更新字段 → `save` → 转 `BookResponse`
  - `delete(Long id)` → 查找 → `deleteById` → 返回 null
  - 实体→DTO 转换私有方法 `toResponse(Book)`
- [ ] 验证：`mvn compile` 成功

#### Task 3.3 — 图书 Controller
- [ ] 创建 `BookController.java`：`@RestController` + `@RequestMapping("/api/books")`
  - `GET /` → `@PreAuthorize("hasAnyRole('ADMIN','READER')")` → `list(@RequestParam page, @RequestParam size)` → `ApiResponse<Page<BookResponse>>`
  - `GET /{id}` → `@PreAuthorize("hasAnyRole('ADMIN','READER')")` → `getById(@PathVariable Long id)` → `ApiResponse<BookResponse>`
  - `POST /` → `@PreAuthorize("hasRole('ADMIN')")` → `create(@Valid @RequestBody BookRequest)` → `ResponseEntity.status(201).body(...)`
  - `PUT /{id}` → `@PreAuthorize("hasRole('ADMIN')")` → `update(@PathVariable, @Valid @RequestBody BookRequest)`
  - `DELETE /{id}` → `@PreAuthorize("hasRole('ADMIN')")` → `delete(@PathVariable Long id)` → `ApiResponse<Void>` data=null
- [ ] 验证：`mvn compile` 成功；启动后端，携带 JWT 调用各端点返回正确结果

---

### Phase 4: 前端项目骨架与基础层 (library-frontend)

#### Task 4.1 — 初始化 Vite + React + TS 项目
- [ ] 创建 `package.json`：依赖 `react`(18) / `react-dom`(18) / `react-router-dom`(6) / `axios`；devDependencies `vite`(5) / `@vitejs/plugin-react` / `typescript` / `@types/react` / `@types/react-dom`
- [ ] 创建 `vite.config.ts`：配置 `@vitejs/plugin-react` 插件、`server.proxy` 将 `/api` 代理到 `http://localhost:8080`
- [ ] 创建 `tsconfig.json`：`target: ES2020`、`jsx: react-jsx`、`strict: true`、`paths` 别名 `@/* → src/*`
- [ ] 创建 `index.html`：`<div id="root">`，引入 `/src/main.tsx`
- [ ] 验证：`npm install` + `npm run dev` 成功启动 5173 端口

#### Task 4.2 — TypeScript 类型定义
- [ ] 创建 `src/types/book.ts`：
  - `interface Book { id: number; title: string; author: string; isbn: string; publisher?: string; category?: string; stock: number; totalStock: number; createdAt: string; updatedAt: string; }`
  - `interface BookRequest { title: string; author: string; isbn: string; publisher?: string; category?: string; totalStock: number; }`
  - `interface ApiResponse<T> { code: number; message: string; data: T; }`
  - `interface PageResponse<T> { content: T[]; page: number; size: number; totalElements: number; totalPages: number; }`
  - `interface LoginResponse { token: string; role: string; username: string; }`
- [ ] 验证：`npx tsc --noEmit` 无类型错误

#### Task 4.3 — API 客户端层
- [ ] 创建 `src/api/client.ts`：Axios 实例，请求拦截器从 `localStorage` 读取 token 注入 `Authorization: Bearer <token>` 头；响应拦截器：401 时清除 token 并跳转 `/login`
- [ ] 创建 `src/api/books.ts`：封装 `getBooks(page, size)` / `getBook(id)` / `createBook(data)` / `updateBook(id, data)` / `deleteBook(id)` 方法，均返回 `Promise`
- [ ] 验证：`npx tsc --noEmit` 无类型错误

#### Task 4.4 — 认证状态管理
- [ ] 创建 `src/context/AuthContext.tsx`：
  - `AuthContext` 持有 `token` / `role` / `username` / `login(token, role, username)` / `logout()`
  - 从 `localStorage` 初始化 token 状态
  - `useAuth()` 自定义 hook 消费 context
  - `ProtectedRoute` 组件：未登录跳转 `/login`，非 ADMIN 访问管理路由跳转 `/books`
- [ ] 验证：`npx tsc --noEmit` 无类型错误

---

### Phase 5: 前端页面与路由 (library-frontend)

#### Task 5.1 — 登录页
- [ ] 创建 `src/pages/Login.tsx`：用户名+密码表单，提交调用 `POST /api/auth/login`（通过 axios），成功后存储 token/role/username 到 `AuthContext` + `localStorage`，跳转 `/books`；失败显示错误提示
- [ ] 验证：`npm run dev` 启动，登录页可提交表单

#### Task 5.2 — 图书表格组件
- [ ] 创建 `src/components/BookTable.tsx`：接收 `books: Book[]` + 回调 props（`onEdit`/`onDelete`），渲染表格列（书名/作者/ISBN/出版社/分类/库存/总库存），管理员角色显示编辑/删除操作按钮
- [ ] 验证：`npx tsc --noEmit` 无类型错误

#### Task 5.3 — 图书列表页
- [ ] 创建 `src/pages/BookList.tsx`：
  - `useEffect` 调用 `getBooks(page, size)` 获取分页数据
  - 渲染 `BookTable` 组件展示数据
  - 分页控件（上一页/下一页/页码）
  - 管理员显示「新增图书」按钮，跳转 `/books/new`
  - 编辑跳转 `/books/:id/edit`，删除调用 `deleteBook` 后刷新列表
- [ ] 验证：`npm run dev` 启动，列表页可渲染数据（需后端运行）

#### Task 5.4 — 图书表单页
- [ ] 创建 `src/pages/BookForm.tsx`：
  - 根据 URL 参数 `id` 判断新增/编辑模式
  - 编辑模式时 `useEffect` 调用 `getBook(id)` 预填表单
  - 表单字段：书名/作者/ISBN/出版社/分类/总库存，含前端校验
  - 提交调用 `createBook` 或 `updateBook`，成功后跳转 `/books`
- [ ] 验证：`npx tsc --noEmit` 无类型错误

#### Task 5.5 — 路由与应用入口
- [ ] 创建 `src/App.tsx`：`BrowserRouter` + `Routes` 定义 `/login` / `/books` / `/books/new` / `/books/:id/edit`，用 `ProtectedRoute` 包裹受保护路由
- [ ] 创建 `src/main.tsx`：`ReactDOM.createRoot` 挂载 `<App />`，外层包裹 `AuthProvider`
- [ ] 验证：`npm run dev` 全站路由可正常跳转

---

### Phase 6: 集成联调与验收 (library-frontend + library-backend)

#### Task 6.1 — 前后端联调
- [ ] 启动 MySQL 8（确保 `library_db` 数据库存在）
- [ ] 启动后端 `mvn spring-boot:run`（端口 8080，Flyway 建表 + 种子数据）
- [ ] 启动前端 `npm run dev`（端口 5173，Vite 代理 `/api` → 8080）
- [ ] 验证登录流程：admin/admin123 → 获取 JWT → 跳转图书列表
- [ ] 验证图书 CRUD 全流程：新增 → 列表可见 → 编辑 → 详情正确 → 删除 → 列表消失

#### Task 6.2 — 权限与边界验收
- [ ] 读者角色登录后，图书列表可见但新增/编辑/删除按钮隐藏
- [ ] 读者角色直接调用 `POST /api/books` 返回 403
- [ ] 无 token 访问 `/api/books` 返回 401
- [ ] ISBN 重复新增返回 400 错误提示
- [ ] 查询不存在的图书 ID 返回 404

---

## 5. 跨仓对齐点 (Cross-Repo Contract Alignment)

| 对齐项 | 后端定义 (library-backend) | 前端消费 (library-frontend) | 对齐方式 |
|--------|--------------------------|---------------------------|---------|
| 统一响应格式 | `ApiResponse<T>` {code, message, data} | `ApiResponse<T>` 类型定义 | TypeScript interface 镜像 |
| 分页格式 | `Page<T>` {content, page, size, totalElements, totalPages} | `PageResponse<T>` 类型定义 | TypeScript interface 镜像 |
| 字段命名 | 实体 `totalStock`/`createdAt`/`updatedAt` (驼峰) | `Book` interface 字段名一致 | JSON 序列化驼峰，无需转换 |
| JWT 传递 | `Authorization: Bearer <token>` 请求头 | Axios 拦截器自动注入 | 请求拦截器统一处理 |
| 端点路径 | `/api/auth/login` + `/api/books` + CRUD | `api/books.ts` 封装对应路径 | 路径硬编码对齐 |
| 时间格式 | `@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")` | 字符串字段直接展示 | ISO 8601 字符串透传 |
| 错误码 | HTTP 200 + body.code (200/400/401/403/404/500) | Axios 响应拦截器按 code 处理 | 响应拦截器统一处理 |
| CORS | `CorsConfig` 允许 `localhost:5173` | Vite dev server proxy `/api` | 后端 CORS + 前端 proxy 双保险 |

---

## 6. 依赖关系与执行顺序

```
Phase 1 (后端骨架+数据层)
  └─▶ Phase 2 (后端安全+认证) ──依赖 Repository + DTO
        └─▶ Phase 3 (后端业务层) ──依赖 Security + DTO + 异常
              └─▶ Phase 6.1 (联调) ──依赖后端 API 可用
                          ▲
Phase 4 (前端骨架+基础层)   │
  └─▶ Phase 5 (前端页面) ───┘ ──依赖 API 客户端 + AuthContext
```

> Phase 1-3（后端）与 Phase 4-5（前端）可并行执行，Phase 6 需前后端均就绪后进行。

---

## 7. 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| MySQL 环境未就绪 | 后端无法启动 | application.yml 配置本地 MySQL，V1 脚本自动建表 |
| JPA `ddl-auto` 与 Flyway 冲突 | 表结构不一致 | `ddl-auto: validate`，由 Flyway 独家管理 DDL |
| JWT 密钥硬编码 | 安全风险 | 从 `application.yml` 注入，开发期使用占位密钥 |
| 前后端字段命名不一致 | 运行时解析失败 | 后端 JSON 默认驼峰序列化，前端 TS 类型同步镜像 |
| 读者越权操作 | 安全漏洞 | 前端隐藏入口 + 后端 `@PreAuthorize` 双重拦截 |

---

## 8. 验收检查清单

- [ ] 后端 `mvn compile` 零错误
- [ ] 后端启动 Flyway V1+V2 执行成功
- [ ] 登录接口返回有效 JWT
- [ ] 图书 CRUD 五个端点全部通过
- [ ] 权限矩阵验证通过（ADMIN 全权限，READER 仅读）
- [ ] 前端 `npx tsc --noEmit` 零错误
- [ ] 前端 `npm run dev` 启动成功
- [ ] 全链路联调通过（登录→列表→新增→编辑→删除）
- [ ] 跨仓类型对齐点全部一致

---

*本计划基于系分设计规格说明书产出，遵循 writing-plans 技能方法论，任务粒度可独立执行与验证。*

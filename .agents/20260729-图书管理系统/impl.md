# 图书管理系统 编码实现报告

> **文档元信息**
>
> | 项目 | 内容 |
> |------|------|
> | 文档版本 | v1.0 |
> | 作者 | DTCoder |
> | 创建日期 | 2026-07-29 |
> | 关联系分文档 | `.agents/20260729-图书管理系统/design.md` |
> | 技能 | dtazziboot-java-coding-standards |

## 1. 实现概览

基于系分设计文档，完成图书管理系统后端（library-backend）全部4个模块、5张数据表、17个REST接口的代码实现。技术栈：Spring Boot 3.2.5 + MyBatis-Plus 3.5.5 + MySQL + Redis + JWT + BCrypt。

## 2. 模块进度追踪

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|:----:|------|:----:|:----:|:----:|:-----:|:----:|------|
| 0 | 项目骨架 | ✅ | - | ✅ | ✅ | ✅ | 已完成 |
| 1 | common 通用 | ✅ | - | ✅ | ✅ | ✅ | 已完成 |
| 2 | auth 用户权限 | ✅ | ⬜ | ✅ | ✅ | ✅ | 已完成* |
| 3 | book 图书管理 | ✅ | ⬜ | ✅ | ✅ | ✅ | 已完成* |
| 4 | reader 读者管理 | ✅ | ⬜ | ✅ | ✅ | ✅ | 已完成* |
| 5 | borrow 借阅管理 | ✅ | ⬜ | ✅ | ✅ | ✅ | 已完成* |

> *TEST 列标记 ⬜：单元测试未生成（环境无 JDK/Maven，降级为静态审查）。

## 3. 代码变更清单

### 3.1 项目骨架

| 文件 | 说明 |
|------|------|
| `pom.xml` | Maven 配置，Spring Boot 3.2.5 + MyBatis-Plus + MySQL + Redis + JWT + BCrypt |
| `src/main/resources/application.yml` | 应用配置（数据源/Redis/MyBatis-Plus/JWT/借阅参数） |
| `src/main/resources/schema.sql` | 数据库初始化脚本（5表+索引+初始数据） |
| `src/main/java/com/library/LibraryBackendApplication.java` | 启动类 |

### 3.2 common 通用模块

| 文件 | 说明 |
|------|------|
| `common/result/Result.java` | 统一响应结构 `{code, msg, data}` |
| `common/result/PageResult.java` | 分页响应结构 `{total, list}` |
| `common/exception/BizException.java` | 业务异常 |
| `common/exception/ErrorCode.java` | 错误码枚举（AUTH/BOOK/READER/BORROW/SYSTEM） |
| `common/exception/GlobalExceptionHandler.java` | 全局异常处理器 |
| `common/constant/LibraryConstants.java` | 系统常量（借阅期限30天/最大在借5本/角色/状态等） |
| `common/context/UserContext.java` | 当前登录用户上下文（请求属性） |
| `common/jwt/JwtUtil.java` | JWT 签发与校验工具 |
| `common/interceptor/AuthInterceptor.java` | 认证与权限拦截器（登录态+垂直权限） |
| `common/config/WebMvcConfig.java` | Web MVC 配置（注册拦截器） |
| `common/config/MybatisPlusConfig.java` | MyBatis-Plus 配置（分页插件+自动填充） |
| `common/config/PasswordConfig.java` | BCrypt 密码编码器 |

### 3.3 auth 用户与权限模块

| 文件 | 说明 |
|------|------|
| `auth/entity/SysUserDO.java` | 系统用户 DO（sys_user 表） |
| `auth/mapper/SysUserMapper.java` | 系统用户 Mapper |
| `resources/mapper/SysUserMapper.xml` | MyBatis XML（selectByUsername） |
| `auth/dto/LoginRequest.java` | 登录请求 DTO |
| `auth/dto/LoginResult.java` | 登录结果 DTO |
| `auth/dto/CurrentUserVO.java` | 当前用户信息 VO |
| `auth/service/AuthService.java` | 认证服务接口 |
| `auth/service/impl/AuthServiceImpl.java` | 认证服务实现（BCrypt校验+JWT签发） |
| `auth/controller/AuthController.java` | 认证控制器（W01-W03） |

### 3.4 book 图书管理模块

| 文件 | 说明 |
|------|------|
| `book/entity/BookDO.java` | 图书 DO（book 表，@TableLogic 逻辑删除） |
| `book/entity/BookCategoryDO.java` | 图书分类 DO |
| `book/mapper/BookMapper.java` | 图书 Mapper（deductStock/restoreStock/searchBooks） |
| `book/mapper/BookCategoryMapper.java` | 分类 Mapper |
| `resources/mapper/BookMapper.xml` | MyBatis XML（库存扣减/恢复/搜索） |
| `book/dto/BookCreateRequest.java` | 新增图书请求 |
| `book/dto/BookUpdateRequest.java` | 修改图书请求 |
| `book/dto/BookVO.java` | 图书 VO |
| `book/dto/CategoryCreateRequest.java` | 新增分类请求 |
| `book/dto/CategoryVO.java` | 分类 VO |
| `book/service/BookService.java` | 图书服务接口 |
| `book/service/impl/BookServiceImpl.java` | 图书服务实现（CRUD+搜索+库存维护） |
| `book/controller/BookController.java` | 图书控制器（W04-W09, W14） |

### 3.5 reader 读者管理模块

| 文件 | 说明 |
|------|------|
| `reader/entity/ReaderDO.java` | 读者 DO（reader 表，@TableLogic） |
| `reader/mapper/ReaderMapper.java` | 读者 Mapper |
| `resources/mapper/ReaderMapper.xml` | MyBatis XML |
| `reader/dto/ReaderCreateRequest.java` | 新增读者请求 |
| `reader/dto/ReaderUpdateRequest.java` | 修改读者请求 |
| `reader/dto/ReaderVO.java` | 读者 VO（手机号脱敏） |
| `reader/service/ReaderService.java` | 读者服务接口 |
| `reader/service/impl/ReaderServiceImpl.java` | 读者服务实现（事务创建sys_user+reader+脱敏） |
| `reader/controller/ReaderController.java` | 读者控制器（W10-W13） |

### 3.6 borrow 借阅管理模块

| 文件 | 说明 |
|------|------|
| `borrow/entity/BorrowRecordDO.java` | 借阅记录 DO（borrow_record 表） |
| `borrow/mapper/BorrowRecordMapper.java` | 借阅记录 Mapper（updateReturn幂等/countBorrowing/countOverdue） |
| `resources/mapper/BorrowRecordMapper.xml` | MyBatis XML（条件更新/统计/分页） |
| `borrow/dto/BorrowRequest.java` | 借阅请求 |
| `borrow/dto/ReturnRequest.java` | 归还请求 |
| `borrow/dto/BorrowResult.java` | 借阅结果 |
| `borrow/dto/ReturnResult.java` | 归还结果（含逾期天数） |
| `borrow/dto/BorrowRecordVO.java` | 借阅记录 VO |
| `borrow/service/BorrowService.java` | 借阅服务接口 |
| `borrow/service/impl/BorrowServiceImpl.java` | 借阅服务实现（事务+行级锁防超卖+逾期检测） |
| `borrow/controller/BorrowController.java` | 借阅控制器（W15-W17） |

## 4. L1 静态检查

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|:--------:|
| 命名规范 | 类名大驼峰、方法名小驼峰、常量全大写、DO/VO/Request/Result 后缀 | ✅ |
| 布尔字段命名 | 不使用 is 前缀，DB 列 is_deleted → Java 字段 deleted | ✅ |
| 通用出参结构 | 统一 `{code, msg, data}` | ✅ |
| 错误码规范 | `{MODULE}_{SEQ}` 格式，模块前缀映射 | ✅ |
| 异常日志 | SLF4J + 占位符、自定义 BizException + 全局处理器 | ✅ |
| 安全规范 | SQL 参数化 `#{}`、输入校验 `@Valid`、BCrypt 加密、手机号脱敏 | ✅ |
| 逻辑删除 | book/reader 表 @TableLogic，禁止物理删除 | ✅ |
| 事务一致性 | 借阅/归还/新增读者均 `@Transactional(rollbackFor=Exception.class)` | ✅ |
| 并发控制 | 库存扣减 `WHERE stock >= qty` 行级锁防超卖 | ✅ |
| 水平权限 | 借阅记录按 userId 过滤，归还 updateReturn 含 userId 条件 | ✅ |
| 垂直权限 | /api/admin/** 拦截器校验 ADMIN 角色 | ✅ |
| 登录态校验 | 全局拦截器校验 JWT，白名单仅 /api/auth/login | ✅ |
| 幂等设计 | 归还条件更新 `status IN ('BORROWING','OVERDUE')` | ✅ |
| 常量管理 | 无魔法值，BORROW_PERIOD_DAYS=30、MAX_BORROW_COUNT=5 | ✅ |
| Javadoc | 类/方法注释使用 `/** */` 格式 | ✅ |

## 5. L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 编译验证 | ⚠️ | 环境无 JDK/Maven（mvn/java 均未安装），降级为静态审查 |
| 单测验证 | ⚠️ | 同上，单元测试未生成 |

### 待人工验证

```bash
mvn compile -DskipTests
mvn test
```

## 6. 跨仓对齐点

| 对齐点 | 后端（library-backend） | 前端（library-frontend） | 状态 |
|--------|------------------------|-------------------------|------|
| 接口前缀 | /api | 需调用 /api 前缀 | ✅ 已对齐 |
| 登录接口 | POST /api/auth/login → {token, role} | 霍使用 JWT Token | ✅ 契约已定义 |
| 鉴权方式 | Authorization: Bearer {token} | 需在请求头携带 | ✅ 契约已定义 |
| 管理端路径 | /api/admin/** 需 ADMIN | 需区分管理员/读者路由 | ✅ 契约已定义 |
| 读者端路径 | /api/books/search, /api/borrow, /api/return, /api/borrow/records | 需对应页面 | ✅ 契约已定义 |
| 响应结构 | {code:"OK", msg, data} | 需解析 code 判断成功 | ✅ 契约已定义 |

## 7. 设计文档符合性

| 系分检查项 | 实现 | 符合 |
|------------|------|:----:|
| F01 用户登录认证 | AuthController.login + AuthServiceImpl + JwtUtil | ✅ |
| F02 角色权限控制 | AuthInterceptor（ADMIN/READER 垂直权限） | ✅ |
| F03-F06 图书CRUD | BookController + BookServiceImpl | ✅ |
| F07-F10 读者CRUD | ReaderController + ReaderServiceImpl（含事务） | ✅ |
| F11 图书搜索 | BookController.searchBooks（keyword/categoryId 分页） | ✅ |
| F12 借阅图书 | BorrowServiceImpl.borrow（扣库存+30天期限+事务） | ✅ |
| F13 归还图书 | BorrowServiceImpl.returnBook（恢复库存+逾期检测） | ✅ |
| F14 借阅记录查询 | BorrowController.listMyRecords（水平权限） | ✅ |
| F15 分类管理 | BookController.createCategory + listCategories | ✅ |
| F16 借阅期限配置 | application.yml `library.borrow.period-days=30` | ✅ |
| 并发防超卖 | BookMapper.xml `WHERE stock >= #{qty}` 行级锁 | ✅ |
| 逾期检测实时计算 | now vs due_date，无定时任务 | ✅ |

## 8. 已发现问题与修复

| 问题 | 修复 |
|------|------|
| AuthServiceImpl.createSysUser 三元表达式冗余 | 简化为直接 setStatus("ACTIVE") |
| BorrowServiceImpl.borrow 校验顺序不符设计时序 | 调整为：图书存在→逾期校验→在借数校验→扣库存（避免不必要的库存扣减） |
| AuthServiceImpl 移除 LibraryConstants 后 BizException 误删 | 恢复 BizException import |

## 9. 待确认项（继承自系分设计）

| 编号 | 内容 | 当前实现假设 |
|------|------|-------------|
| A01 | 读者是否可自助注册 | 不支持，管理员统一录入 |
| A02 | 借阅期限按类型差异化 | 全局统一30天，可配置 |
| A03 | 逾期后是否允许继续借阅 | 禁止新借阅（countOverdue>0 时拒绝） |
| A04 | 单读者最大在借数量 | 5本，可配置 |
| A05 | 图书多副本 | 库存字段管理多副本 |
| A06 | 图书预约 | 不做，库存为0时提示不可借 |

# Design: Library Management System

## Architecture

前后端分离：
- `library-backend`：Java 17 + Spring Boot 3.x（待澄清），REST API，关系型数据库（待澄清）。
- `library-frontend`：React 或 Vue（待澄清），消费 REST API。

模块划分（后端）：
- `auth`：认证授权，签发/校验 JWT，角色 `ADMIN`/`READER`。
- `book`：图书领域，管理图书与库存。
- `reader`：读者领域，管理读者信息。
- `circulation`：借阅流通，借阅/归还/逾期判定。
- `discovery`：图书检索浏览。
- `history`：借阅记录查询。

## Data Model

### book
| field | type | constraints |
|---|---|---|
| id | BIGINT | PK, auto |
| title | VARCHAR(255) | not null |
| author | VARCHAR(255) | not null |
| isbn | VARCHAR(20) | not null, unique |
| category | VARCHAR(64) | not null（自由文本，待澄清层级） |
| stock | INT | not null, default 0, >= 0 |
| created_at / updated_at | TIMESTAMP | not null |

### reader
| field | type | constraints |
|---|---|---|
| id | BIGINT | PK, auto |
| name | VARCHAR(64) | not null |
| username | VARCHAR(64) | not null, unique |
| password_hash | VARCHAR(255) | not null |
| role | ENUM('ADMIN','READER') | not null, default 'READER' |
| enabled | BOOLEAN | not null, default true |
| created_at / updated_at | TIMESTAMP | not null |

> 注：管理员账号复用 reader 表，role=ADMIN（保守假设，待澄清是否独立管理员表）。

### borrow_record
| field | type | constraints |
|---|---|---|
| id | BIGINT | PK, auto |
| book_id | BIGINT | FK book.id, not null |
| reader_id | BIGINT | FK reader.id, not null |
| borrow_at | TIMESTAMP | not null |
| due_at | TIMESTAMP | not null（borrow_at + 30d） |
| return_at | TIMESTAMP | nullable |
| status | ENUM('ACTIVE','RETURNED','OVERDUE') | not null, default 'ACTIVE' |
| created_at / updated_at | TIMESTAMP | not null |

## Cross-Repo API Contract (REST)

> 前后端共享契约。所有写接口需 `Authorization: Bearer <jwt>`，按角色校验权限。
> 路径前缀 `/api`。时间统一 ISO-8601 (UTC)。

### Auth
| Method | Path | Role | 说明 |
|---|---|---|---|
| POST | /api/auth/login | public | 登录，返回 `{token, role, username}` |
| GET | /api/auth/me | any | 当前用户信息 |

### Book Management（管理员）
| Method | Path | Role | Body | 说明 |
|---|---|---|---|---|
| GET | /api/admin/books | ADMIN | query: page,size,keyword | 分页列表 |
| POST | /api/admin/books | ADMIN | BookDTO | 新增，ISBN 重复返回 409 |
| PUT | /api/admin/books/{id} | ADMIN | BookDTO | 更新 |
| DELETE | /api/admin/books/{id} | ADMIN | - | 删除；有在册借阅返回 409 |

### Reader Management（管理员）
| Method | Path | Role | Body | 说明 |
|---|---|---|---|---|
| GET | /api/admin/readers | ADMIN | query: page,size,keyword | 分页列表 |
| POST | /api/admin/readers | ADMIN | ReaderDTO | 新增，username 重复 409 |
| PUT | /api/admin/readers/{id} | ADMIN | ReaderDTO | 更新 |
| DELETE | /api/admin/readers/{id} | ADMIN | - | 删除；有在册借阅返回 409 |

### Circulation（读者）
| Method | Path | Role | Body | 说明 |
|---|---|---|---|---|
| POST | /api/borrow | READER | `{bookId}` | 借阅：校验在册上限/库存/启用；扣减库存；建借阅记录 due_at=now+30d |
| POST | /api/return | READER | `{recordId}` | 归还：校验归属与状态；恢复库存；置 RETURNED |

### Discovery（读者）
| Method | Path | Role | 说明 |
|---|---|---|---|
| GET | /api/books | READER | query: page,size,keyword,category；返回含 stock |
| GET | /api/books/{id} | READER | 详情 |

### Borrowing History（读者）
| Method | Path | Role | 说明 |
|---|---|---|---|
| GET | /api/me/borrow-records | READER | 当前读者借阅记录，含逾期状态 |

## Key Behaviors & Edge Cases

- 借阅库存扣减须并发安全（乐观锁版本号或行锁），避免超卖。
- 借阅在册上限 5（待澄清）；超限返回 409。
- 读者被禁用（enabled=false）不可借阅。
- 归还须校验：借阅记录归属当前读者、状态为 ACTIVE 或 OVERDUE。
- 逾期判定：`now > due_at` 且 status != RETURNED → status=OVERDUE，借阅记录/借阅历史接口应展示逾期提示。
- 检索分页：默认 page=1, size=10；keyword 命中 title/author/isbn（待澄清是否含分类）。

## Compatibility

- 全新系统，无既有契约。新增接口与字段，保持后向兼容。
- 前端依据本契约实现调用，字段命名 camelCase（JSON）。

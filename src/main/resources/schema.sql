-- 图书管理系统数据库 schema
-- 适配 H2 (MySQL 模式) 内存数据库

-- 图书表
CREATE TABLE IF NOT EXISTS book (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title       VARCHAR(200)  NOT NULL COMMENT '书名',
    author      VARCHAR(100)  NOT NULL COMMENT '作者',
    isbn        VARCHAR(20)   NOT NULL COMMENT 'ISBN',
    category    VARCHAR(50)  NOT NULL COMMENT '分类',
    stock       INT           NOT NULL DEFAULT 0 COMMENT '库存数量',
    total_stock INT           NOT NULL DEFAULT 0 COMMENT '总库存数量(用于统计借出)',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_book_isbn UNIQUE (isbn)
);

-- 读者表
CREATE TABLE IF NOT EXISTS reader (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(50)  NOT NULL COMMENT '读者姓名',
    phone       VARCHAR(20)  NOT NULL COMMENT '联系电话',
    email       VARCHAR(100) COMMENT '邮箱',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    book_id       BIGINT       NOT NULL COMMENT '图书ID',
    reader_id     BIGINT       NOT NULL COMMENT '读者ID',
    borrow_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    due_time      TIMESTAMP    NOT NULL COMMENT '应还时间',
    return_time   TIMESTAMP    COMMENT '实际归还时间',
    status        VARCHAR(20)  NOT NULL DEFAULT 'BORROWED' COMMENT '状态: BORROWED-借阅中, RETURNED-已归还, OVERDUE-逾期',
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES book(id),
    CONSTRAINT fk_borrow_reader FOREIGN KEY (reader_id) REFERENCES reader(id)
);

-- 初始化演示数据
INSERT INTO book (title, author, isbn, category, stock, total_stock) VALUES
    ('Java核心技术 卷I', 'Cay S. Horstmann', '9787115546081', '计算机', 3, 3),
    ('深入理解Java虚拟机', '周志明', '9787111269607', '计算机', 2, 2),
    ('三体', '刘慈欣', '9787536692930', '科幻', 5, 5),
    ('活着', '余华', '9787506365437', '文学', 4, 4)
ON CONFLICT (isbn) DO NOTHING;

INSERT INTO reader (name, phone, email) VALUES
    ('张三', '13800138000', 'zhangsan@example.com'),
    ('李四', '13900139000', 'lisi@example.com'),
    ('王五', '13700137000', NULL)
ON CONFLICT DO NOTHING;

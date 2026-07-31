-- 图书管理系统 DDL
-- 对应设计文档 5.1.1.1 / 5.2.1.1 / 5.3.1.1

DROP TABLE IF EXISTS borrow_record;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS reader;

CREATE TABLE book (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    title        VARCHAR(200) NOT NULL COMMENT '书名',
    author       VARCHAR(100) NOT NULL COMMENT '作者',
    isbn         VARCHAR(20)  NOT NULL COMMENT 'ISBN编号',
    publisher    VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    category     VARCHAR(50)  DEFAULT NULL COMMENT '图书分类',
    stock        INT          NOT NULL DEFAULT 0 COMMENT '当前可借库存',
    total_stock  INT          NOT NULL DEFAULT 0 COMMENT '总馆藏数量',
    description  VARCHAR(500) DEFAULT NULL COMMENT '图书简介',
    is_deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否逻辑删除：0-否，1-是',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_book_isbn (isbn),
    KEY idx_book_title (title),
    KEY idx_book_author (author)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';

CREATE TABLE reader (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    name         VARCHAR(50)  NOT NULL COMMENT '姓名',
    phone        VARCHAR(20)  NOT NULL COMMENT '手机号',
    reader_type  VARCHAR(20)  NOT NULL COMMENT '读者类型',
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '读者状态',
    is_deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否逻辑删除：0-否，1-是',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_reader_phone (phone),
    KEY idx_reader_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='读者信息表';

CREATE TABLE borrow_record (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    book_id      BIGINT       NOT NULL COMMENT '图书ID',
    reader_id    BIGINT       NOT NULL COMMENT '读者ID',
    borrow_time  DATETIME     NOT NULL COMMENT '借阅时间',
    due_time     DATETIME     NOT NULL COMMENT '应还时间',
    return_time  DATETIME     DEFAULT NULL COMMENT '实际归还时间',
    status       VARCHAR(20)  NOT NULL DEFAULT 'BORROWING' COMMENT '借阅状态',
    is_overdue   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否逾期：0-否，1-是',
    is_deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否逻辑删除：0-否，1-是',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_borrow_record_book_id (book_id),
    KEY idx_borrow_record_reader_id (reader_id),
    KEY idx_borrow_record_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

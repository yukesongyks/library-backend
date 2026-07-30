-- 图书管理系统建表脚本（H2 兼容 MySQL 模式）
CREATE TABLE IF NOT EXISTS book (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    author      VARCHAR(255) NOT NULL,
    isbn        VARCHAR(20)  NOT NULL,
    category    VARCHAR(64)  NOT NULL,
    stock       INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_book_isbn UNIQUE (isbn)
);

CREATE TABLE IF NOT EXISTS reader (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(64)  NOT NULL,
    username      VARCHAR(64)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(10)  NOT NULL DEFAULT 'READER',
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_reader_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS borrow_record (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id    BIGINT NOT NULL,
    reader_id  BIGINT NOT NULL,
    borrow_at  TIMESTAMP NOT NULL,
    due_at     TIMESTAMP NOT NULL,
    return_at  TIMESTAMP,
    status     VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_borrow_book   FOREIGN KEY (book_id)   REFERENCES book(id),
    CONSTRAINT fk_borrow_reader FOREIGN KEY (reader_id) REFERENCES reader(id)
);

CREATE INDEX IF NOT EXISTS idx_borrow_reader_status ON borrow_record(reader_id, status);
CREATE INDEX IF NOT EXISTS idx_book_category ON book(category);

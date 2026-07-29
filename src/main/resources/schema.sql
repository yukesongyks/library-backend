-- ============================================================
-- 图书管理系统 数据库初始化脚本
-- 数据库: library
-- ============================================================

CREATE DATABASE IF NOT EXISTS library DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE library;

-- ----------------------------
-- 1. sys_user 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    `username`     VARCHAR(64)  NOT NULL COMMENT '用户名/登录账号',
    `password`     VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密存储)',
    `role`         VARCHAR(16)  NOT NULL COMMENT '角色:ADMIN/READER',
    `status`       VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态:ACTIVE/FROZEN',
    `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_username` (`username`),
    KEY `idx_sys_user_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 2. book_category 图书分类表
-- ----------------------------
DROP TABLE IF EXISTS `book_category`;
CREATE TABLE `book_category` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    `name`         VARCHAR(64)  NOT NULL COMMENT '分类名称',
    `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_book_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- ----------------------------
-- 3. book 图书信息表
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    `title`        VARCHAR(128) NOT NULL COMMENT '书名',
    `author`       VARCHAR(64)  NOT NULL COMMENT '作者',
    `isbn`         VARCHAR(20)  NOT NULL COMMENT 'ISBN编号',
    `category_id`  BIGINT       NOT NULL COMMENT '分类ID',
    `stock`        INT          NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    `is_deleted`   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
    `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_book_isbn` (`isbn`),
    KEY `idx_book_category` (`category_id`),
    KEY `idx_book_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';

-- ----------------------------
-- 4. reader 读者信息表
-- ----------------------------
DROP TABLE IF EXISTS `reader`;
CREATE TABLE `reader` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    `user_id`      BIGINT       NOT NULL COMMENT '关联sys_user.id',
    `reader_no`    VARCHAR(32)  NOT NULL COMMENT '读者编号/学号',
    `phone`        VARCHAR(20)  NOT NULL COMMENT '联系电话',
    `name`         VARCHAR(64)  NOT NULL COMMENT '读者姓名',
    `is_deleted`   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
    `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reader_user_id` (`user_id`),
    UNIQUE KEY `uk_reader_no` (`reader_no`),
    KEY `idx_reader_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='读者信息表';

-- ----------------------------
-- 5. borrow_record 借阅记录表
-- ----------------------------
DROP TABLE IF EXISTS `borrow_record`;
CREATE TABLE `borrow_record` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    `book_id`      BIGINT       NOT NULL COMMENT '关联book.id',
    `user_id`      BIGINT       NOT NULL COMMENT '读者关联sys_user.id',
    `borrow_date`  DATETIME     NOT NULL COMMENT '借阅时间',
    `due_date`     DATETIME     NOT NULL COMMENT '应还时间(借阅+30天)',
    `return_date`  DATETIME     NULL COMMENT '实际归还时间',
    `status`       VARCHAR(16)  NOT NULL DEFAULT 'BORROWING' COMMENT '状态:BORROWING/RETURNED/OVERDUE',
    `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_borrow_user` (`user_id`, `status`),
    KEY `idx_borrow_book` (`book_id`),
    KEY `idx_borrow_due` (`due_date`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- ----------------------------
-- 初始化数据: 管理员账号 + 默认分类
-- ----------------------------
-- 初始管理员账号: admin01 / admin123 (仅开发环境，生产环境请通过接口创建)
INSERT INTO `sys_user` (`username`, `password`, `role`, `status`) VALUES
('admin01', '$2b$10$XUXJGYWU6bIfy2Lq1esAJuEseHsc8V.RZRAMwp5.HWm.s3Ugs/x4y', 'ADMIN', 'ACTIVE');

INSERT INTO `book_category` (`name`) VALUES
('计算机'),
('文学'),
('历史'),
('科学');

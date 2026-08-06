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

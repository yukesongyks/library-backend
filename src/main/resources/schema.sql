-- 算法调用埋点表
CREATE TABLE IF NOT EXISTS `algo_call_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `algorithm_type` VARCHAR(32) NOT NULL COMMENT '算法类型: HELLO_WORLD/HASH/BUBBLE_SORT',
    `user_id` VARCHAR(64) NOT NULL COMMENT '调用用户ID',
    `user_type` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '人员类型',
    `user_level` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '人员层级',
    `department` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '人员部门',
    `call_time` DATETIME NOT NULL COMMENT '调用时间',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_algo_user` (`user_id`),
    KEY `idx_algo_type_time` (`algorithm_type`, `call_time`),
    KEY `idx_algo_time` (`call_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='算法调用埋点表';

-- 系统用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户名',
    `user_type` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '人员类型',
    `user_level` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '人员层级',
    `department` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '人员部门',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

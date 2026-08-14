CREATE TABLE IF NOT EXISTS api_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_path VARCHAR(255) NOT NULL COMMENT '接口路径',
    caller_id VARCHAR(64) COMMENT '调用人ID',
    caller_name VARCHAR(128) COMMENT '调用人姓名',
    caller_type VARCHAR(32) COMMENT '人员类型',
    caller_level VARCHAR(16) COMMENT '人员层级',
    caller_dept VARCHAR(128) COMMENT '人员部门',
    call_time DATETIME NOT NULL COMMENT '调用时间',
    duration_ms INT COMMENT '耗时(ms)',
    success TINYINT DEFAULT 1 COMMENT '1成功 0失败',
    INDEX idx_api_path (api_path),
    INDEX idx_caller_type (caller_type),
    INDEX idx_caller_level (caller_level),
    INDEX idx_caller_dept (caller_dept),
    INDEX idx_call_time (call_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API调用埋点记录表';
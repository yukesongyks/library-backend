CREATE TABLE IF NOT EXISTS demo_call_log (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_type        VARCHAR(32) NOT NULL COMMENT '接口类型: HELLOWORLD/HASH/BUBBLE_SORT',
    caller_id       VARCHAR(64) NOT NULL COMMENT '调用者ID',
    caller_name     VARCHAR(128) COMMENT '调用者姓名',
    person_type     VARCHAR(32) COMMENT '人员类型: 正式/实习/外包',
    person_level    VARCHAR(32) COMMENT '人员层级: P5/P6/P7/P8...',
    department      VARCHAR(128) COMMENT '所属部门',
    request_params  TEXT COMMENT '请求参数(JSON)',
    response_data   TEXT COMMENT '响应结果(JSON)',
    execution_time_ms INT COMMENT '执行耗时(ms)',
    call_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
    INDEX idx_api_type (api_type),
    INDEX idx_caller_id (caller_id),
    INDEX idx_call_time (call_time),
    INDEX idx_department (department),
    INDEX idx_person_type (person_type),
    INDEX idx_person_level (person_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功能演示接口调用记录';

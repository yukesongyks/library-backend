CREATE TABLE IF NOT EXISTS api_call_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_type VARCHAR(32) NOT NULL,
    user_id VARCHAR(64),
    user_name VARCHAR(128),
    personnel_type VARCHAR(32),
    personnel_level VARCHAR(32),
    department VARCHAR(128),
    request_payload TEXT,
    response_payload TEXT,
    duration_ms BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_api_type ON api_call_log(api_type);
CREATE INDEX IF NOT EXISTS idx_department ON api_call_log(department);
CREATE INDEX IF NOT EXISTS idx_personnel_type ON api_call_log(personnel_type);
CREATE INDEX IF NOT EXISTS idx_personnel_level ON api_call_log(personnel_level);
CREATE INDEX IF NOT EXISTS idx_created_at ON api_call_log(created_at);

-- DDL: biz_call_record 调用埋点记录表
CREATE TABLE IF NOT EXISTS `biz_call_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
  `biz_type`      VARCHAR(32)  NOT NULL DEFAULT ''     COMMENT '业务类型: HELLOWORLD/HASH/BUBBLE_SORT',
  `caller_id`     VARCHAR(64)  NOT NULL DEFAULT ''     COMMENT '调用人ID',
  `caller_name`   VARCHAR(64)  NOT NULL DEFAULT ''     COMMENT '调用人姓名',
  `caller_type`   VARCHAR(32)  NOT NULL DEFAULT ''     COMMENT '人员类型',
  `caller_level`  VARCHAR(32)  NOT NULL DEFAULT ''     COMMENT '人员层级',
  `caller_dept`   VARCHAR(128) NOT NULL DEFAULT ''     COMMENT '人员部门',
  `cost_ms`       BIGINT       NOT NULL DEFAULT 0      COMMENT '调用耗时(毫秒)',
  `result`        VARCHAR(16)  NOT NULL DEFAULT 'SUCCESS' COMMENT '调用结果: SUCCESS/FAIL',
  `gmt_create`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`pk_biz_call_record` (id)),
  KEY `idx_call_record_biz_type` (`biz_type`),
  KEY `idx_call_record_caller_id` (`caller_id`),
  KEY `idx_call_record_gmt_create` (`gmt_create`),
  KEY `idx_call_record_type_level` (`caller_type`, `caller_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调用埋点记录表';

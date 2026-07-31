package com.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 审计配置
 * <p>
 * 配合实体上的 createTime / updateTime 自动填充.
 *
 * @author library-team
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}

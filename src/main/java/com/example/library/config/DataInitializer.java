package com.example.library.config;

import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据初始化：插入若干样例 User（不同类型/层级/部门），便于报表演示。
 * 对应 tasks B4。
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Transactional
    public ApplicationRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("样例 User 已存在，跳过初始化");
                return;
            }
            userRepository.save(new User("alice", "alice@example.com", "内部", "P6", "技术部"));
            userRepository.save(new User("bob", "bob@example.com", "内部", "P7", "产品部"));
            userRepository.save(new User("carol", "carol@example.com", "外部", "P5", "市场部"));
            userRepository.save(new User("dave", "dave@example.com", "内部", "P8", "技术部"));
            userRepository.save(new User("eve", "eve@example.com", "外部", "P6", "运营部"));
            log.info("已插入 5 条样例 User");
        };
    }
}

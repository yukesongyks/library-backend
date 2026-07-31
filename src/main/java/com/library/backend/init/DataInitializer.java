package com.library.backend.init;

import com.library.backend.model.User;
import com.library.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 用户数据初始化器。
 *
 * <p>应用启动时预置若干不同维度（人员类型/层级/部门）的 User 记录，
 * 供埋点切面按 {@code X-User-Id} 关联人员维度使用。使用 {@code findByUsername}
 * 去重，重启 H2 内存库不会产生重复数据。</p>
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        saveIfAbsent(1L, "alice", "开发", "P6", "技术部");
        saveIfAbsent(2L, "bob", "测试", "P5", "技术部");
        saveIfAbsent(3L, "carol", "产品", "P7", "产品部");
        saveIfAbsent(4L, "dave", "开发", "P7", "技术部");
        saveIfAbsent(5L, "eve", "运维", "M1", "运维部");
        saveIfAbsent(6L, "frank", "测试", "P6", "产品部");
        log.info("用户维度数据初始化完成，共 {} 条", userRepository.count());
    }

    private void saveIfAbsent(Long id, String username, String personnelType,
                              String personnelLevel, String department) {
        userRepository.findByUsername(username).orElseGet(() -> {
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setPersonnelType(personnelType);
            user.setPersonnelLevel(personnelLevel);
            user.setDepartment(department);
            return userRepository.save(user);
        });
    }
}

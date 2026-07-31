package com.library.backend.repository;

import com.library.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户 Repository。
 *
 * <p>按 username 查询用于 {@code DataInitializer} 去重；按 id 查询用于埋点切面关联人员维度。</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}

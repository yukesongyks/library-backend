package com.library.reader;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Long> {

    Optional<Reader> findByUsername(String username);

    boolean existsByUsername(String username);

    /**
     * 管理员分页列表：keyword 命中 name/username。
     */
    @Query("""
            SELECT r FROM Reader r
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(r.username) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY r.id DESC
            """)
    Page<Reader> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

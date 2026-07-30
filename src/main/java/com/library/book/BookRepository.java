package com.library.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    /**
     * 管理员分页列表：keyword 命中 title/author/isbn。
     */
    @Query("""
            SELECT b FROM Book b
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY b.id DESC
            """)
    Page<Book> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 读者检索：keyword 命中 title/author/isbn，category 过滤。
     */
    @Query("""
            SELECT b FROM Book b
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:category IS NULL OR :category = '' OR b.category = :category)
            ORDER BY b.id DESC
            """)
    Page<Book> search(@Param("keyword") String keyword,
                      @Param("category") String category,
                      Pageable pageable);
}

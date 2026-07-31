package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 图书 DAO 层
 *
 * @author library-team
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * 根据ISBN查询图书
     *
     * @param isbn ISBN编号
     * @return 图书对象(可能为空)
     */
    Optional<Book> findByIsbn(String isbn);
}

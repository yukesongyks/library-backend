package com.antgroup.library.book.mapper;

import com.antgroup.library.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书 Mapper（设计文档 S06/S07/S08 暴露库存扣减/恢复/查询实体方法）。
 */
@Mapper
public interface BookMapper {

    Book selectById(@Param("id") Long id);

    Book selectByIsbn(@Param("isbn") String isbn);

    List<Book> selectPage(@Param("offset") int offset,
                          @Param("pageSize") int pageSize,
                          @Param("title") String title,
                          @Param("author") String author,
                          @Param("isbn") String isbn);

    long selectCount(@Param("title") String title,
                     @Param("author") String author,
                     @Param("isbn") String isbn);

    int insert(Book book);

    int update(Book book);

    int logicDelete(@Param("id") Long id);

    /**
     * 乐观锁库存扣减：UPDATE book SET stock=stock-1 WHERE id=? AND stock>0。
     */
    int deductStock(@Param("bookId") Long bookId, @Param("quantity") int quantity);

    /**
     * 库存恢复：UPDATE book SET stock=stock+? WHERE id=?。
     */
    int restoreStock(@Param("bookId") Long bookId, @Param("quantity") int quantity);
}

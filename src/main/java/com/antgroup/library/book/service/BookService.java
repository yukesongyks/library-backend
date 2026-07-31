package com.antgroup.library.book.service;

import com.antgroup.library.book.dto.BookCreateRequest;
import com.antgroup.library.book.dto.BookQueryRequest;
import com.antgroup.library.book.dto.BookUpdateRequest;
import com.antgroup.library.book.dto.BookVO;
import com.antgroup.library.book.entity.Book;
import com.antgroup.library.common.response.PageResult;

/**
 * 图书服务（设计文档 S01-S08）。
 */
public interface BookService {

    PageResult<BookVO> queryBooks(BookQueryRequest request);

    BookVO getBookById(Long id);

    Long createBook(BookCreateRequest request);

    void updateBook(Long id, BookUpdateRequest request);

    void deleteBook(Long id);

    void deductStock(Long bookId, int quantity);

    void restoreStock(Long bookId, int quantity);

    Book getBookEntity(Long id);

    /**
     * 查询指定图书的未归还借阅数量（供图书删除/修改ISBN 校验使用）。
     */
    int countUnreturnedByBook(Long bookId);
}

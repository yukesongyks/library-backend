package com.library.service;

import com.library.common.PageResult;
import com.library.model.dto.BookRequest;
import com.library.model.dto.BookVO;

import java.util.List;

/**
 * 图书 Service
 *
 * @author library
 */
public interface BookService {

    /**
     * 分页搜索图书
     *
     * @param keyword  关键词(书名/作者)
     * @param category 分类
     * @param pageNum  页码
     * @param pageSize 每页条数
     */
    PageResult<BookVO> searchBooks(String keyword, String category, int pageNum, int pageSize);

    /**
     * 根据ID查询图书
     */
    BookVO getBookById(Long id);

    /**
     * 查询全部图书
     */
    List<BookVO> listAllBooks();

    /**
     * 新增图书
     */
    Long addBook(BookRequest request);

    /**
     * 更新图书
     */
    void updateBook(Long id, BookRequest request);

    /**
     * 删除图书
     */
    void deleteBook(Long id);

    /**
     * 扣减库存(借阅时调用)
     *
     * @param bookId 图书ID
     * @param count  扣减数量
     */
    void deductStock(Long bookId, int count);

    /**
     * 恢复库存(归还时调用)
     *
     * @param bookId 图书ID
     * @param count  恢复数量
     */
    void restoreStock(Long bookId, int count);
}

package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.PageResult;

/**
 * 图书业务服务接口
 *
 * @author library-team
 */
public interface BookService {

    /**
     * 分页查询图书
     *
     * @param pageNum  页码(从1开始)
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<BookResponse> listBooks(Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询图书
     *
     * @param id 图书ID
     * @return 图书响应DTO
     */
    BookResponse getBookById(Long id);

    /**
     * 新增图书
     *
     * @param request 图书请求DTO
     * @return 新增后的图书响应DTO
     */
    BookResponse createBook(BookRequest request);

    /**
     * 更新图书
     *
     * @param id      图书ID
     * @param request 图书请求DTO
     * @return 更新后的图书响应DTO
     */
    BookResponse updateBook(Long id, BookRequest request);

    /**
     * 删除图书
     *
     * @param id 图书ID
     */
    void deleteBook(Long id);
}

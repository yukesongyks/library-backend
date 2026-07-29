package com.library.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.book.dto.BookCreateRequest;
import com.library.book.dto.BookUpdateRequest;
import com.library.book.dto.BookVO;
import com.library.book.dto.CategoryCreateRequest;
import com.library.book.dto.CategoryVO;
import com.library.book.entity.BookDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 图书管理服务接口。
 *
 * @author DTCoder
 */
public interface BookService {

    /**
     * 新增图书。
     *
     * @param req 新增请求
     * @return 新建图书ID
     */
    Long createBook(BookCreateRequest req);

    /**
     * 删除图书（逻辑删除）。
     *
     * @param id 图书ID
     */
    void deleteBook(Long id);

    /**
     * 修改图书。
     *
     * @param id  图书ID
     * @param req 修改请求
     */
    void updateBook(Long id, BookUpdateRequest req);

    /**
     * 分页查询图书（管理端）。
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<BookVO> pageBooks(Integer pageNum, Integer pageSize);

    /**
     * 搜索浏览图书（读者端）。
     *
     * @param keyword    搜索关键词
     * @param categoryId 分类ID
     * @param pageNum     页码
     * @param pageSize    每页条数
     * @return 分页结果
     */
    IPage<BookVO> searchBooks(String keyword, Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询图书。
     *
     * @param id 图书ID
     * @return 图书记录
     */
    BookDO getBookById(Long id);

    /**
     * 扣减库存（行级锁防超卖）。
     *
     * @param bookId 图书ID
     * @param qty    数量
     * @return true=扣减成功
     */
    boolean deductStock(Long bookId, int qty);

    /**
     * 恢复库存。
     *
     * @param bookId 图书ID
     * @param qty    数量
     */
    void restoreStock(Long bookId, int qty);

    /**
     * 新增分类。
     *
     * @param req 新增请求
     * @return 分类ID
     */
    Long createCategory(CategoryCreateRequest req);

    /**
     * 查询分类列表。
     *
     * @return 分类列表
     */
    List<CategoryVO> listCategories();

    /**
     * 批量获取图书ID到书名的映射。
     *
     * @param bookIds 图书ID集合
     * @return ID → 书名映射
     */
    Map<Long, String> getBookTitleMap(Collection<Long> bookIds);

    /**
     * 校验分类是否存在。
     *
     * @param categoryId 分类ID
     * @return true=存在
     */
    boolean existsCategory(Long categoryId);
}

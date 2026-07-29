package com.library.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.book.dto.BookCreateRequest;
import com.library.book.dto.BookUpdateRequest;
import com.library.book.dto.BookVO;
import com.library.book.dto.CategoryCreateRequest;
import com.library.book.dto.CategoryVO;
import com.library.book.entity.BookCategoryDO;
import com.library.book.entity.BookDO;
import com.library.book.mapper.BookCategoryMapper;
import com.library.book.mapper.BookMapper;
import com.library.book.service.BookService;
import com.library.common.exception.BizException;
import com.library.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 图书管理服务实现。
 *
 * @author DTCoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookCategoryMapper bookCategoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBook(BookCreateRequest req) {
        // R01: ISBN全局唯一
        LambdaQueryWrapper<BookDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookDO::getIsbn, req.getIsbn());
        Long count = bookMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException(ErrorCode.BOOK_001);
        }

        // R02: 分类必须存在
        if (!existsCategory(req.getCategoryId())) {
            throw new BizException(ErrorCode.BOOK_002);
        }

        BookDO book = new BookDO();
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setIsbn(req.getIsbn());
        book.setCategoryId(req.getCategoryId());
        book.setStock(req.getStock());
        bookMapper.insert(book);
        log.info("新增图书成功: id={}, isbn={}", book.getId(), book.getIsbn());
        return book.getId();
    }

    @Override
    public void deleteBook(Long id) {
        BookDO book = bookMapper.selectById(id);
        if (book == null) {
            throw new BizException(ErrorCode.BOOK_005);
        }
        bookMapper.deleteById(id);
        log.info("删除图书成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(Long id, BookUpdateRequest req) {
        BookDO book = bookMapper.selectById(id);
        if (book == null) {
            throw new BizException(ErrorCode.BOOK_005);
        }

        if (req.getTitle() != null) {
            book.setTitle(req.getTitle());
        }
        if (req.getAuthor() != null) {
            book.setAuthor(req.getAuthor());
        }
        if (req.getCategoryId() != null) {
            if (!existsCategory(req.getCategoryId())) {
                throw new BizException(ErrorCode.BOOK_002);
            }
            book.setCategoryId(req.getCategoryId());
        }
        if (req.getStock() != null) {
            book.setStock(req.getStock());
        }
        bookMapper.updateById(book);
        log.info("修改图书成功: id={}", id);
    }

    @Override
    public IPage<BookVO> pageBooks(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<BookDO> page = new Page<>(pageNum, pageSize);
        IPage<BookDO> bookPage = bookMapper.selectPage(page, null);
        return bookPage.convert(this::toBookVO);
    }

    @Override
    public IPage<BookVO> searchBooks(String keyword, Long categoryId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<BookDO> page = new Page<>(pageNum, pageSize);
        IPage<BookDO> bookPage = bookMapper.searchBooks(page, keyword, categoryId);
        return bookPage.convert(this::toBookVO);
    }

    @Override
    public BookDO getBookById(Long id) {
        return bookMapper.selectById(id);
    }

    @Override
    public boolean deductStock(Long bookId, int qty) {
        int rows = bookMapper.deductStock(bookId, qty);
        return rows > 0;
    }

    @Override
    public void restoreStock(Long bookId, int qty) {
        bookMapper.restoreStock(bookId, qty);
    }

    @Override
    public Long createCategory(CategoryCreateRequest req) {
        LambdaQueryWrapper<BookCategoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCategoryDO::getName, req.getName());
        Long count = bookCategoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException(ErrorCode.BOOK_002);
        }
        BookCategoryDO category = new BookCategoryDO();
        category.setName(req.getName());
        bookCategoryMapper.insert(category);
        log.info("新增分类成功: id={}, name={}", category.getId(), category.getName());
        return category.getId();
    }

    @Override
    public List<CategoryVO> listCategories() {
        List<BookCategoryDO> list = bookCategoryMapper.selectList(null);
        return list.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            return vo;
        }).toList();
    }

    @Override
    public boolean existsCategory(Long categoryId) {
        return bookCategoryMapper.selectById(categoryId) != null;
    }

    /**
     * DO 转 VO。
     */
    private BookVO toBookVO(BookDO book) {
        BookVO vo = new BookVO();
        vo.setId(book.getId());
        vo.setTitle(book.getTitle());
        vo.setAuthor(book.getAuthor());
        vo.setIsbn(book.getIsbn());
        vo.setCategoryId(book.getCategoryId());
        vo.setStock(book.getStock());
        if (book.getCategoryId() != null) {
            BookCategoryDO category = bookCategoryMapper.selectById(book.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        return vo;
    }
}

package com.library.service.impl;

import com.library.common.BusinessConstants;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.common.ResultCode;
import com.library.dao.mapper.BookMapper;
import com.library.model.dto.BookRequest;
import com.library.model.dto.BookVO;
import com.library.model.entity.BookDO;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书 Service 实现
 *
 * @author library
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    @Override
    public PageResult<BookVO> searchBooks(String keyword, String category, int pageNum, int pageSize) {
        pageNum = Math.max(pageNum, BusinessConstants.DEFAULT_PAGE_NUM);
        pageSize = pageSize <= 0 ? BusinessConstants.DEFAULT_PAGE_SIZE : pageSize;

        long total = bookMapper.selectCount(keyword, category);
        if (total <= 0) {
            return PageResult.empty(pageNum, pageSize);
        }

        int offset = (pageNum - 1) * pageSize;
        List<BookDO> bookList = bookMapper.selectPage(keyword, category, offset, pageSize);
        List<BookVO> voList = bookList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageNum, pageSize, total, voList);
    }

    @Override
    public BookVO getBookById(Long id) {
        BookDO book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND, "图书不存在: " + id);
        }
        return convertToVO(book);
    }

    @Override
    public List<BookVO> listAllBooks() {
        return bookMapper.selectAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addBook(BookRequest request) {
        BookDO existBook = bookMapper.selectByIsbn(request.getIsbn());
        if (existBook != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "ISBN已存在: " + request.getIsbn());
        }

        BookDO book = new BookDO();
        BeanUtils.copyProperties(request, book);
        bookMapper.insert(book);
        log.info("新增图书成功, id={}, isbn={}", book.getId(), book.getIsbn());
        return book.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(Long id, BookRequest request) {
        BookDO book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND, "图书不存在: " + id);
        }

        BookDO updateDO = new BookDO();
        updateDO.setId(id);
        BeanUtils.copyProperties(request, updateDO);
        bookMapper.update(updateDO);
        log.info("更新图书成功, id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Long id) {
        BookDO book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND, "图书不存在: " + id);
        }
        bookMapper.deleteById(id);
        log.info("删除图书成功, id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long bookId, int count) {
        if (count <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "扣减数量必须大于0");
        }
        BookDO book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND, "图书不存在: " + bookId);
        }
        if (book.getStock() < count) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH,
                    "库存不足, 当前库存: " + book.getStock() + ", 需求: " + count);
        }
        int rows = bookMapper.deductStock(bookId, count);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH, "库存扣减失败, 可能库存已不足");
        }
        log.info("扣减库存成功, bookId={}, count={}", bookId, count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreStock(Long bookId, int count) {
        if (count <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "恢复数量必须大于0");
        }
        BookDO book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND, "图书不存在: " + bookId);
        }
        bookMapper.restoreStock(bookId, count);
        log.info("恢复库存成功, bookId={}, count={}", bookId, count);
    }

    /**
     * DO 转 VO
     */
    private BookVO convertToVO(BookDO book) {
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);
        vo.setBorrowedCount(book.getTotalStock() - book.getStock());
        return vo;
    }
}

package com.antgroup.library.book.service.impl;

import com.antgroup.library.book.dto.BookCreateRequest;
import com.antgroup.library.book.dto.BookQueryRequest;
import com.antgroup.library.book.dto.BookUpdateRequest;
import com.antgroup.library.book.dto.BookVO;
import com.antgroup.library.book.entity.Book;
import com.antgroup.library.book.mapper.BookMapper;
import com.antgroup.library.book.service.BookService;
import com.antgroup.library.borrow.mapper.BorrowRecordMapper;
import com.antgroup.library.common.exception.BizException;
import com.antgroup.library.common.exception.ErrorCode;
import com.antgroup.library.common.response.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 图书服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    public PageResult<BookVO> queryBooks(BookQueryRequest request) {
        int pageNum = request.normalizedPageNum();
        int pageSize = request.normalizedPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = bookMapper.selectCount(request.getTitle(), request.getAuthor(), request.getIsbn());
        List<BookVO> voList = new ArrayList<>();
        if (total == 0) {
            return PageResult.of(0, pageNum, pageSize, voList);
        }

        List<Book> books = bookMapper.selectPage(offset, pageSize, request.getTitle(),
                request.getAuthor(), request.getIsbn());
        for (Book book : books) {
            voList.add(convertToVO(book));
        }
        return PageResult.of(total, pageNum, pageSize, voList);
    }

    @Override
    public BookVO getBookById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BizException(ErrorCode.BOOK_001);
        }
        return convertToVO(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBook(BookCreateRequest request) {
        Book exists = bookMapper.selectByIsbn(request.getIsbn());
        if (exists != null) {
            throw new BizException(ErrorCode.BOOK_002);
        }
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublisher(request.getPublisher());
        book.setCategory(request.getCategory());
        book.setStock(request.getStock());
        book.setTotalStock(request.getStock());
        book.setDescription(request.getDescription());
        bookMapper.insert(book);
        return book.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(Long id, BookUpdateRequest request) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BizException(ErrorCode.BOOK_001);
        }
        if (request.getIsbn() != null && !request.getIsbn().equals(book.getIsbn())) {
            int unreturned = borrowRecordMapper.countUnreturnedByBook(id);
            if (unreturned > 0) {
                throw new BizException(ErrorCode.BOOK_003);
            }
            Book isbnExists = bookMapper.selectByIsbn(request.getIsbn());
            if (isbnExists != null && !isbnExists.getId().equals(id)) {
                throw new BizException(ErrorCode.BOOK_002);
            }
        }
        Book update = new Book();
        update.setId(id);
        update.setTitle(request.getTitle());
        update.setAuthor(request.getAuthor());
        update.setIsbn(request.getIsbn());
        update.setPublisher(request.getPublisher());
        update.setCategory(request.getCategory());
        update.setDescription(request.getDescription());
        int rows = bookMapper.update(update);
        if (rows == 0) {
            throw new BizException(ErrorCode.BOOK_001);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BizException(ErrorCode.BOOK_001);
        }
        int unreturned = borrowRecordMapper.countUnreturnedByBook(id);
        if (unreturned > 0) {
            throw new BizException(ErrorCode.BOOK_004);
        }
        bookMapper.logicDelete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long bookId, int quantity) {
        int rows = bookMapper.deductStock(bookId, quantity);
        if (rows == 0) {
            throw new BizException(ErrorCode.BOOK_005);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreStock(Long bookId, int quantity) {
        bookMapper.restoreStock(bookId, quantity);
    }

    @Override
    public Book getBookEntity(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BizException(ErrorCode.BOOK_001);
        }
        return book;
    }

    @Override
    public int countUnreturnedByBook(Long bookId) {
        return borrowRecordMapper.countUnreturnedByBook(bookId);
    }

    private BookVO convertToVO(Book book) {
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);
        return vo;
    }
}

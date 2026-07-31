package com.library.service.impl;

import com.library.common.enums.ResultCode;
import com.library.common.exception.BusinessException;
import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.PageResult;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 图书业务服务实现
 *
 * @author library-team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final BookRepository bookRepository;

    @Override
    public PageResult<BookResponse> listBooks(Integer pageNum, Integer pageSize) {
        int actualPageNum = (pageNum == null || pageNum < 1) ? DEFAULT_PAGE_NUM : pageNum;
        int actualPageSize = (pageSize == null || pageSize < 1)
                ? DEFAULT_PAGE_SIZE
                : Math.min(pageSize, MAX_PAGE_SIZE);

        PageRequest pageRequest = PageRequest.of(
                actualPageNum - 1,
                actualPageSize,
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        Page<Book> page = bookRepository.findAll(pageRequest);
        List<BookResponse> list = page.getContent().stream()
                .map(BookResponse::of)
                .collect(Collectors.toList());

        return new PageResult<>(
                actualPageNum,
                actualPageSize,
                page.getTotalElements(),
                page.getTotalPages(),
                list
        );
    }

    @Override
    public BookResponse getBookById(Long id) {
        Book book = findBookById(id);
        return BookResponse.of(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookResponse createBook(BookRequest request) {
        // ISBN唯一性校验
        Optional<Book> existed = bookRepository.findByIsbn(request.getIsbn());
        if (existed.isPresent()) {
            log.warn("新增图书失败, ISBN已存在: isbn={}", request.getIsbn());
            throw new BusinessException(ResultCode.BOOK_ISBN_DUPLICATED);
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublisher(request.getPublisher());
        book.setStock(request.getStock());

        Book saved = bookRepository.save(book);
        log.info("新增图书成功: id={}, isbn={}", saved.getId(), saved.getIsbn());
        return BookResponse.of(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBookById(id);

        // ISBN变更时校验唯一性
        if (!book.getIsbn().equals(request.getIsbn())) {
            Optional<Book> existed = bookRepository.findByIsbn(request.getIsbn());
            if (existed.isPresent() && !existed.get().getId().equals(id)) {
                log.warn("更新图书失败, ISBN已存在: isbn={}", request.getIsbn());
                throw new BusinessException(ResultCode.BOOK_ISBN_DUPLICATED);
            }
            book.setIsbn(request.getIsbn());
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setStock(request.getStock());

        Book updated = bookRepository.save(book);
        log.info("更新图书成功: id={}", updated.getId());
        return BookResponse.of(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Long id) {
        Book book = findBookById(id);
        bookRepository.delete(book);
        log.info("删除图书成功: id={}", id);
    }

    /**
     * 根据ID查询图书, 不存在则抛业务异常
     */
    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.BOOK_NOT_FOUND));
    }
}

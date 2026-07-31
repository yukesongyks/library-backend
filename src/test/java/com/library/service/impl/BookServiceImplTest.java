package com.library.service.impl;

import com.library.common.enums.ResultCode;
import com.library.common.exception.BusinessException;
import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.PageResult;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * BookServiceImpl 单元测试
 * <p>
 * 遵循 FIRST 原则, 使用 Mockito 隔离 Repository 依赖.
 *
 * @author library-team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("图书服务单元测试")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book mockBook;

    @BeforeEach
    void setUp() {
        mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("Java核心技术");
        mockBook.setAuthor("Cay S. Horstmann");
        mockBook.setIsbn("9787111111111");
        mockBook.setPublisher("机械工业出版社");
        mockBook.setStock(10);
        mockBook.setCreateTime(LocalDateTime.now());
        mockBook.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("分页查询图书 - 返回分页结果")
    void listBooks_shouldReturnPageResult() {
        List<Book> books = Collections.singletonList(mockBook);
        Page<Book> page = new PageImpl<>(books);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResult<BookResponse> result = bookService.listBooks(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("Java核心技术", result.getList().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("分页查询图书 - 参数为空时使用默认值")
    void listBooks_nullParams_shouldUseDefaults() {
        List<Book> books = Collections.singletonList(mockBook);
        Page<Book> page = new PageImpl<>(books);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResult<BookResponse> result = bookService.listBooks(null, null);

        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
    }

    @Test
    @DisplayName("根据ID查询图书 - 存在时返回正确数据")
    void getBookById_exists_shouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        BookResponse response = bookService.getBookById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java核心技术", response.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("根据ID查询图书 - 不存在时抛出业务异常")
    void getBookById_notExists_shouldThrowBusinessException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> bookService.getBookById(999L)
        );

        assertEquals(ResultCode.BOOK_NOT_FOUND.getCode(), exception.getResultCode().getCode());
    }

    @Test
    @DisplayName("新增图书 - ISBN不重复时创建成功")
    void createBook_newIsbn_shouldCreate() {
        BookRequest request = buildRequest();
        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        BookResponse response = bookService.createBook(request);

        assertNotNull(response);
        assertEquals(mockBook.getId(), response.getId());
        verify(bookRepository, times(1)).findByIsbn(request.getIsbn());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("新增图书 - ISBN重复时抛出业务异常")
    void createBook_duplicatedIsbn_shouldThrowBusinessException() {
        BookRequest request = buildRequest();
        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Optional.of(mockBook));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> bookService.createBook(request)
        );

        assertEquals(ResultCode.BOOK_ISBN_DUPLICATED.getCode(), exception.getResultCode().getCode());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("更新图书 - 存在且ISBN未变更时更新成功")
    void updateBook_exists_sameIsbn_shouldUpdate() {
        BookRequest request = buildRequest();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        BookResponse response = bookService.updateBook(1L, request);

        assertNotNull(response);
        verify(bookRepository, never()).findByIsbn(any());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("更新图书 - 不存在时抛出业务异常")
    void updateBook_notExists_shouldThrowBusinessException() {
        BookRequest request = buildRequest();
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> bookService.updateBook(999L, request)
        );

        assertEquals(ResultCode.BOOK_NOT_FOUND.getCode(), exception.getResultCode().getCode());
    }

    @Test
    @DisplayName("删除图书 - 存在时删除成功")
    void deleteBook_exists_shouldDelete() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(any(Book.class));
    }

    @Test
    @DisplayName("删除图书 - 不存在时抛出业务异常")
    void deleteBook_notExists_shouldThrowBusinessException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> bookService.deleteBook(999L)
        );

        assertEquals(ResultCode.BOOK_NOT_FOUND.getCode(), exception.getResultCode().getCode());
    }

    /**
     * 构造测试请求DTO
     */
    private BookRequest buildRequest() {
        BookRequest request = new BookRequest();
        request.setTitle("Java核心技术");
        request.setAuthor("Cay S. Horstmann");
        request.setIsbn("9787111111111");
        request.setPublisher("机械工业出版社");
        request.setStock(10);
        return request;
    }
}

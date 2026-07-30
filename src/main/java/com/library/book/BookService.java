package com.library.book;

import com.library.book.dto.BookDTO;
import com.library.book.dto.BookVO;
import com.library.common.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * 新增图书。ISBN 重复返回 409。
     */
    @Transactional
    public BookVO create(BookDTO dto) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new BusinessException(409, "ISBN已存在: " + dto.getIsbn());
        }
        Book book = new Book(
                dto.getTitle(), dto.getAuthor(), dto.getIsbn(),
                dto.getCategory(), dto.getStock()
        );
        Book saved = bookRepository.save(book);
        return new BookVO(saved);
    }

    /**
     * 更新图书。
     */
    @Transactional
    public BookVO update(Long id, BookDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "图书不存在"));
        // ISBN 变更时校验唯一
        if (!book.getIsbn().equals(dto.getIsbn()) && bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new BusinessException(409, "ISBN已存在: " + dto.getIsbn());
        }
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCategory(dto.getCategory());
        book.setStock(dto.getStock());
        Book saved = bookRepository.save(book);
        return new BookVO(saved);
    }

    /**
     * 删除图书前的校验：有在册借阅（ACTIVE/OVERDUE）则返回 409。
     * 由调用方（Controller）调用 BorrowRecordService 校验后调用此方法。
     */
    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "图书不存在"));
        bookRepository.delete(book);
    }

    /**
     * 查找单本图书（不存在抛 404）。
     */
    @Transactional(readOnly = true)
    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "图书不存在"));
    }

    @Transactional(readOnly = true)
    public BookVO getByIdVO(Long id) {
        return new BookVO(getById(id));
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * 暴露仓储给 Controller 进行分页查询。
     */
    @Transactional(readOnly = true)
    public BookRepository getBookRepository() {
        return bookRepository;
    }
}

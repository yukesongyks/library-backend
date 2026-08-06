package com.library.service;

import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.dto.response.PageResponse;
import com.library.entity.Book;
import com.library.exception.BusinessException;
import com.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public PageResponse<BookResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findAll(pageable);
        Page<BookResponse> responsePage = bookPage.map(this::toResponse);
        return PageResponse.from(responsePage);
    }

    public BookResponse getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "图书不存在"));
        return toResponse(book);
    }

    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException(400, "ISBN已存在");
        }
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublisher(request.getPublisher());
        book.setCategory(request.getCategory());
        book.setTotalStock(request.getTotalStock());
        book.setStock(request.getTotalStock());
        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "图书不存在"));
        if (bookRepository.existsByIsbnAndIdNot(request.getIsbn(), id)) {
            throw new BusinessException(400, "ISBN已存在");
        }
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublisher(request.getPublisher());
        book.setCategory(request.getCategory());
        book.setTotalStock(request.getTotalStock());
        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "图书不存在"));
        bookRepository.delete(book);
    }

    private BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setPublisher(book.getPublisher());
        response.setCategory(book.getCategory());
        response.setStock(book.getStock());
        response.setTotalStock(book.getTotalStock());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        return response;
    }
}

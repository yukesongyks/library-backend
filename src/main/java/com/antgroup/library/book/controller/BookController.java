package com.antgroup.library.book.controller;

import com.antgroup.library.book.dto.BookCreateRequest;
import com.antgroup.library.book.dto.BookQueryRequest;
import com.antgroup.library.book.dto.BookUpdateRequest;
import com.antgroup.library.book.dto.BookVO;
import com.antgroup.library.book.service.BookService;
import com.antgroup.library.common.response.PageResult;
import com.antgroup.library.common.response.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 图书管理接口（W01-W05）。
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Result<PageResult<BookVO>> queryBooks(BookQueryRequest request) {
        return Result.success(bookService.queryBooks(request));
    }

    @GetMapping("/{id}")
    public Result<BookVO> getBookById(@PathVariable Long id) {
        return Result.success(bookService.getBookById(id));
    }

    @PostMapping
    public Result<Long> createBook(@Valid @RequestBody BookCreateRequest request) {
        return Result.success(bookService.createBook(request));
    }

    @PutMapping("/{id}")
    public Result<Void> updateBook(@PathVariable Long id, @Valid @RequestBody BookUpdateRequest request) {
        bookService.updateBook(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Result.success();
    }
}

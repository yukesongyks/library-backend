package com.library.controller;

import com.library.common.api.Result;
import com.library.controller.vo.BookPageQuery;
import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.PageResult;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 图书管理RESTful接口
 * <p>
 * API版本: v1
 * 路径前缀: /api/v1/books
 *
 * @author library-team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 分页查询图书
     */
    @GetMapping
    public Result<PageResult<BookResponse>> listBooks(BookPageQuery query) {
        PageResult<BookResponse> page = bookService.listBooks(query.getPageNum(), query.getPageSize());
        return Result.success(page);
    }

    /**
     * 查询单本图书
     */
    @GetMapping("/{id}")
    public Result<BookResponse> getBook(@PathVariable Long id) {
        BookResponse response = bookService.getBookById(id);
        return Result.success(response);
    }

    /**
     * 新增图书
     */
    @PostMapping
    public Result<BookResponse> createBook(@RequestBody @Valid BookRequest request) {
        BookResponse response = bookService.createBook(request);
        return Result.success(response);
    }

    /**
     * 更新图书
     */
    @PutMapping("/{id}")
    public Result<BookResponse> updateBook(@PathVariable Long id,
                                           @RequestBody @Valid BookRequest request) {
        BookResponse response = bookService.updateBook(id, request);
        return Result.success(response);
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Result.success();
    }
}

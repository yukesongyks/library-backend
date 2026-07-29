package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.model.dto.BookRequest;
import com.library.model.dto.BookVO;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 图书 Controller
 *
 * @author library
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 搜索/浏览图书(分页)
     */
    @GetMapping
    public Result<PageResult<BookVO>> searchBooks(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResult<BookVO> page = bookService.searchBooks(keyword, category, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 查询全部图书(不分页, 供下拉选择)
     */
    @GetMapping("/all")
    public Result<List<BookVO>> listAllBooks() {
        return Result.success(bookService.listAllBooks());
    }

    /**
     * 查询图书详情
     */
    @GetMapping("/{id}")
    public Result<BookVO> getBookById(@PathVariable Long id) {
        return Result.success(bookService.getBookById(id));
    }

    /**
     * 新增图书
     */
    @PostMapping
    public Result<Long> addBook(@Valid @RequestBody BookRequest request) {
        return Result.success(bookService.addBook(request));
    }

    /**
     * 更新图书
     */
    @PutMapping("/{id}")
    public Result<Void> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        bookService.updateBook(id, request);
        return Result.success();
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

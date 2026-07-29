package com.library.book.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.book.dto.BookCreateRequest;
import com.library.book.dto.BookUpdateRequest;
import com.library.book.dto.BookVO;
import com.library.book.dto.CategoryCreateRequest;
import com.library.book.dto.CategoryVO;
import com.library.book.service.BookService;
import com.library.common.result.PageResult;
import com.library.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 图书管理控制器（W04-W09, W14）。
 *
 * @author DTCoder
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // ===== 管理端 =====

    /**
     * W04 新增图书。
     */
    @PostMapping("/admin/books")
    public Result<Map<String, Long>> createBook(@Valid @RequestBody BookCreateRequest req) {
        Long id = bookService.createBook(req);
        return Result.success(Map.of("id", id));
    }

    /**
     * W05 删除图书。
     */
    @DeleteMapping("/admin/books/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Result.success();
    }

    /**
     * W06 修改图书。
     */
    @PutMapping("/admin/books/{id}")
    public Result<Void> updateBook(@PathVariable Long id, @Valid @RequestBody BookUpdateRequest req) {
        bookService.updateBook(id, req);
        return Result.success();
    }

    /**
     * W07 分页查询图书（管理端）。
     */
    @GetMapping("/admin/books")
    public Result<PageResult<BookVO>> pageBooks(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        IPage<BookVO> page = bookService.pageBooks(pageNum, pageSize);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    /**
     * W08 新增图书分类。
     */
    @PostMapping("/admin/categories")
    public Result<Map<String, Long>> createCategory(@Valid @RequestBody CategoryCreateRequest req) {
        Long id = bookService.createCategory(req);
        return Result.success(Map.of("id", id));
    }

    /**
     * W09 查询分类列表。
     */
    @GetMapping("/admin/categories")
    public Result<List<CategoryVO>> listCategories() {
        return Result.success(bookService.listCategories());
    }

    // ===== 读者端 =====

    /**
     * W14 搜索浏览图书（读者端）。
     */
    @GetMapping("/books/search")
    public Result<PageResult<BookVO>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        IPage<BookVO> page = bookService.searchBooks(keyword, categoryId, pageNum, pageSize);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }
}

package com.library.discovery;

import com.library.book.Book;
import com.library.book.BookRepository;
import com.library.book.dto.BookVO;
import com.library.common.ApiResponse;
import com.library.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * 读者图书检索控制器。
 * 契约对齐 design.md：
 *   GET /api/books       (READER) — 分页检索，keyword 命中 title/author/isbn，category 过滤
 *   GET /api/books/{id}  (READER) — 详情
 */
@RestController
@RequestMapping("/api/books")
public class DiscoveryController {

    private final BookRepository bookRepository;

    public DiscoveryController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public ApiResponse<PageResponse<BookVO>> search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Book> result = bookRepository.search(keyword, category, pageable);
        Page<BookVO> voPage = result.map(BookVO::new);
        return ApiResponse.ok(PageResponse.of(voPage));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookVO> getById(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new com.library.common.BusinessException(404, "图书不存在"));
        return ApiResponse.ok(new BookVO(book));
    }
}

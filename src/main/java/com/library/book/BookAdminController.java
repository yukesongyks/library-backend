package com.library.book;

import com.library.book.dto.BookDTO;
import com.library.book.dto.BookVO;
import com.library.common.ApiResponse;
import com.library.common.BusinessException;
import com.library.common.PageResponse;
import com.library.circulation.BorrowRecordStatus;
import com.library.circulation.BorrowRecordRepository;
import com.library.circulation.BorrowRecord;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员图书管理 CRUD 控制器。
 * 契约对齐 design.md：GET/POST/PUT/DELETE /api/admin/books
 */
@RestController
@RequestMapping("/api/admin/books")
public class BookAdminController {

    private final BookService bookService;
    private final BorrowRecordRepository borrowRecordRepository;

    public BookAdminController(BookService bookService,
                                BorrowRecordRepository borrowRecordRepository) {
        this.bookService = bookService;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @GetMapping
    public ApiResponse<PageResponse<BookVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Book> result = bookService.getBookRepository().findByKeyword(keyword, pageable);
        Page<BookVO> voPage = result.map(BookVO::new);
        return ApiResponse.ok(PageResponse.of(voPage));
    }

    @PostMapping
    public ApiResponse<BookVO> create(@Valid @RequestBody BookDTO dto) {
        return ApiResponse.ok(bookService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<BookVO> update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        return ApiResponse.ok(bookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        // 校验在册借阅（返回 HTTP 409）
        List<BorrowRecord> activeRecords = borrowRecordRepository
                .findByBookIdAndStatusIn(id, List.of(BorrowRecordStatus.ACTIVE, BorrowRecordStatus.OVERDUE));
        if (!activeRecords.isEmpty()) {
            throw new BusinessException(409, "该书有在册借阅，无法删除");
        }
        bookService.delete(id);
        return ApiResponse.ok();
    }
}

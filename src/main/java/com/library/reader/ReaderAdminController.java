package com.library.reader;

import com.library.common.ApiResponse;
import com.library.common.BusinessException;
import com.library.common.PageResponse;
import com.library.circulation.BorrowRecord;
import com.library.circulation.BorrowRecordRepository;
import com.library.circulation.BorrowRecordStatus;
import com.library.reader.dto.ReaderDTO;
import com.library.reader.dto.ReaderVO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员读者管理 CRUD 控制器。
 * 契约对齐 design.md：GET/POST/PUT/DELETE /api/admin/readers
 */
@RestController
@RequestMapping("/api/admin/readers")
public class ReaderAdminController {

    private final ReaderService readerService;
    private final ReaderRepository readerRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public ReaderAdminController(ReaderService readerService,
                                  ReaderRepository readerRepository,
                                  BorrowRecordRepository borrowRecordRepository) {
        this.readerService = readerService;
        this.readerRepository = readerRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @GetMapping
    public ApiResponse<PageResponse<ReaderVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Reader> result = readerRepository.findByKeyword(keyword, pageable);
        Page<ReaderVO> voPage = result.map(ReaderVO::new);
        return ApiResponse.ok(PageResponse.of(voPage));
    }

    @PostMapping
    public ApiResponse<ReaderVO> create(@Valid @RequestBody ReaderDTO dto) {
        return ApiResponse.ok(readerService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReaderVO> update(@PathVariable Long id, @Valid @RequestBody ReaderDTO dto) {
        return ApiResponse.ok(readerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Reader reader = readerService.getById(id);
        // 校验该读者的在册借阅（返回 HTTP 409）
        List<BorrowRecord> readerActiveRecords = borrowRecordRepository
                .findByReaderIdAndStatusIn(id, List.of(BorrowRecordStatus.ACTIVE, BorrowRecordStatus.OVERDUE));
        if (!readerActiveRecords.isEmpty()) {
            throw new BusinessException(409, "该读者有在册借阅，无法删除");
        }
        readerRepository.delete(reader);
        return ApiResponse.ok();
    }
}

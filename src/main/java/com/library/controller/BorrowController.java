package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.model.dto.BorrowRequest;
import com.library.model.dto.BorrowVO;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 借阅 Controller
 *
 * @author library
 */
@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * 借阅图书
     */
    @PostMapping
    public Result<Long> borrowBook(@Valid @RequestBody BorrowRequest request) {
        return Result.success(borrowService.borrowBook(request));
    }

    /**
     * 归还图书
     */
    @PutMapping("/{id}/return")
    public Result<BorrowVO> returnBook(@PathVariable Long id) {
        BorrowVO vo = borrowService.returnBook(id);
        String message = Boolean.TRUE.equals(vo.getOverdue())
                ? "归还成功, 该图书已逾期 " + vo.getOverdueDays() + " 天"
                : "归还成功";
        return Result.success(message, vo);
    }

    /**
     * 查询读者借阅记录(分页)
     */
    @GetMapping("/reader/{readerId}")
    public Result<PageResult<BorrowVO>> listBorrowRecordsByReader(
            @PathVariable Long readerId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResult<BorrowVO> page = borrowService.listBorrowRecordsByReader(readerId, status, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 手动刷新逾期状态
     */
    @PostMapping("/refresh-overdue")
    public Result<Void> refreshOverdueStatus() {
        borrowService.refreshOverdueStatus();
        return Result.success();
    }
}

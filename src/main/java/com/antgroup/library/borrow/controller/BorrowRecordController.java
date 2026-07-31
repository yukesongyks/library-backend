package com.antgroup.library.borrow.controller;

import com.antgroup.library.borrow.dto.BorrowRecordQueryRequest;
import com.antgroup.library.borrow.dto.BorrowRecordVO;
import com.antgroup.library.borrow.dto.BorrowRequest;
import com.antgroup.library.borrow.service.BorrowRecordService;
import com.antgroup.library.common.response.PageResult;
import com.antgroup.library.common.response.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 借阅管理接口（W11-W13）。
 */
@RestController
@RequestMapping("/api/borrow-records")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @GetMapping
    public Result<PageResult<BorrowRecordVO>> queryRecords(BorrowRecordQueryRequest request) {
        return Result.success(borrowRecordService.queryRecords(request));
    }

    @PostMapping("/borrow")
    public Result<Long> borrowBook(@Valid @RequestBody BorrowRequest request) {
        return Result.success(borrowRecordService.borrowBook(request));
    }

    @PutMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        borrowRecordService.returnBook(id);
        return Result.success();
    }
}

package com.library.borrow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.borrow.dto.BorrowRecordVO;
import com.library.borrow.dto.BorrowRequest;
import com.library.borrow.dto.BorrowResult;
import com.library.borrow.dto.ReturnRequest;
import com.library.borrow.dto.ReturnResult;
import com.library.borrow.service.BorrowService;
import com.library.common.context.UserContext;
import com.library.common.result.PageResult;
import com.library.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 借阅管理控制器（W15-W17）。
 *
 * @author DTCoder
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * W15 借阅图书。
     */
    @PostMapping("/borrow")
    public Result<BorrowResult> borrow(@Valid @RequestBody BorrowRequest req, HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        BorrowResult result = borrowService.borrow(req, userId);
        return Result.success(result);
    }

    /**
     * W16 归还图书。
     */
    @PostMapping("/return")
    public Result<ReturnResult> returnBook(@Valid @RequestBody ReturnRequest req, HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        ReturnResult result = borrowService.returnBook(req, userId);
        return Result.success(result);
    }

    /**
     * W17 查询我的借阅记录。
     */
    @GetMapping("/borrow/records")
    public Result<PageResult<BorrowRecordVO>> listMyRecords(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            HttpServletRequest request) {
        Long userId = UserContext.getUserId(request);
        IPage<BorrowRecordVO> page = borrowService.listMyRecords(userId, status, pageNum, pageSize);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }
}

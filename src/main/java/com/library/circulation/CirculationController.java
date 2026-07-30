package com.library.circulation;

import com.library.auth.JwtPrincipal;
import com.library.circulation.dto.BorrowRecordVO;
import com.library.circulation.dto.BorrowRequest;
import com.library.circulation.dto.ReturnRequest;
import com.library.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅流通控制器。
 * 契约对齐 design.md：
 *   POST /api/borrow  (READER)
 *   POST /api/return (READER)
 *   GET  /api/me/borrow-records (READER)
 */
@RestController
public class CirculationController {

    private final CirculationService circulationService;

    public CirculationController(CirculationService circulationService) {
        this.circulationService = circulationService;
    }

    @PostMapping("/api/borrow")
    public ApiResponse<BorrowRecordVO> borrow(@Valid @RequestBody BorrowRequest req) {
        Long readerId = currentReaderId();
        return ApiResponse.ok(circulationService.borrow(req, readerId));
    }

    @PostMapping("/api/return")
    public ApiResponse<BorrowRecordVO> returnBook(@Valid @RequestBody ReturnRequest req) {
        Long readerId = currentReaderId();
        return ApiResponse.ok(circulationService.returnBook(req, readerId));
    }

    @GetMapping("/api/me/borrow-records")
    public ApiResponse<java.util.List<BorrowRecordVO>> myBorrowRecords() {
        Long readerId = currentReaderId();
        return ApiResponse.ok(circulationService.myBorrowRecords(readerId));
    }

    private Long currentReaderId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtPrincipal principal) {
            return principal.userId();
        }
        throw new com.library.common.BusinessException(401, "未认证");
    }
}

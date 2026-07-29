package com.library.borrow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.book.entity.BookDO;
import com.library.book.service.BookService;
import com.library.borrow.dto.BorrowRecordVO;
import com.library.borrow.dto.BorrowRequest;
import com.library.borrow.dto.BorrowResult;
import com.library.borrow.dto.ReturnRequest;
import com.library.borrow.dto.ReturnResult;
import com.library.borrow.entity.BorrowRecordDO;
import com.library.borrow.mapper.BorrowRecordMapper;
import com.library.borrow.service.BorrowService;
import com.library.common.constant.LibraryConstants;
import com.library.common.exception.BizException;
import com.library.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 借阅管理服务实现。
 *
 * @author DTCoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookService bookService;

    @Value("${library.borrow.period-days:" + LibraryConstants.BORROW_PERIOD_DAYS + "}")
    private int borrowPeriodDays;

    @Value("${library.borrow.max-borrow-count:" + LibraryConstants.MAX_BORROW_COUNT + "}")
    private int maxBorrowCount;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowResult borrow(BorrowRequest req, Long userId) {
        // R01: 图书必须存在且未删除
        BookDO book = bookService.getBookById(req.getBookId());
        if (book == null) {
            throw new BizException(ErrorCode.BORROW_001);
        }

        // R03: 读者无逾期未归还记录（先校验，避免不必要的库存扣减）
        int overdueCount = borrowRecordMapper.countOverdue(userId);
        if (overdueCount > 0) {
            throw new BizException(ErrorCode.BORROW_003);
        }

        // R04: 在借数量<最大值
        int borrowingCount = borrowRecordMapper.countBorrowing(userId);
        if (borrowingCount >= maxBorrowCount) {
            throw new BizException(ErrorCode.BORROW_004);
        }

        // R02 + R05: 库存必须>0（行级锁扣减判断，与生成记录同事务）
        boolean deducted = bookService.deductStock(req.getBookId(), 1);
        if (!deducted) {
            throw new BizException(ErrorCode.BORROW_002);
        }

        // 生成借阅记录
        LocalDateTime now = LocalDateTime.now(ZoneId.of(LibraryConstants.SYSTEM_TIMEZONE));
        LocalDateTime dueDate = now.plusDays(borrowPeriodDays);

        BorrowRecordDO record = new BorrowRecordDO();
        record.setBookId(req.getBookId());
        record.setUserId(userId);
        record.setBorrowDate(now);
        record.setDueDate(dueDate);
        record.setStatus(LibraryConstants.STATUS_BORROWING);
        borrowRecordMapper.insert(record);

        log.info("借阅成功: recordId={}, userId={}, bookId={}, dueDate={}",
                record.getId(), userId, req.getBookId(), dueDate);
        return new BorrowResult(record.getId(), dueDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnResult returnBook(ReturnRequest req, Long userId) {
        // R01: 借阅记录必须存在
        BorrowRecordDO record = borrowRecordMapper.selectById(req.getRecordId());
        if (record == null) {
            throw new BizException(ErrorCode.BORROW_005);
        }

        // R02: 记录归属当前登录读者（水平权限校验）
        // R03: 状态为BORROWING或OVERDUE（幂等：条件更新，影响行数=0则无权或状态不符）
        LocalDateTime now = LocalDateTime.now(ZoneId.of(LibraryConstants.SYSTEM_TIMEZONE));
        boolean isOverdue = now.isAfter(record.getDueDate());
        // 归还始终设为 RETURNED，逾期信息通过 ReturnResult 返回
        String newStatus = LibraryConstants.STATUS_RETURNED;

        int rows = borrowRecordMapper.updateReturn(req.getRecordId(), userId, newStatus, now);
        if (rows == 0) {
            // 判断是无权还是状态不符
            if (!record.getUserId().equals(userId)) {
                throw new BizException(ErrorCode.BORROW_007);
            }
            throw new BizException(ErrorCode.BORROW_006);
        }

        // R04: 恢复库存（同事务）
        bookService.restoreStock(record.getBookId(), 1);

        // R05: 计算逾期天数
        int overdueDays = 0;
        if (isOverdue) {
            overdueDays = (int) Duration.between(record.getDueDate(), now).toDays();
        }

        log.info("归还成功: recordId={}, userId={}, isOverdue={}, overdueDays={}",
                req.getRecordId(), userId, isOverdue, overdueDays);
        return new ReturnResult(isOverdue, overdueDays);
    }

    @Override
    public IPage<BorrowRecordVO> listMyRecords(Long userId, String status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<BorrowRecordDO> page = new Page<>(pageNum, pageSize);
        IPage<BorrowRecordDO> recordPage = borrowRecordMapper.selectByUserId(page, userId, status);
        // 批量获取书名，避免 N+1 查询
        Set<Long> bookIds = recordPage.getRecords().stream()
                .map(BorrowRecordDO::getBookId)
                .collect(Collectors.toSet());
        Map<Long, String> titleMap = bookIds.isEmpty()
                ? Map.of()
                : bookService.getBookTitleMap(bookIds);
        return recordPage.convert(r -> toBorrowRecordVO(r, titleMap));
    }

    @Override
    public boolean isOverdue(Long recordId) {
        BorrowRecordDO record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BizException(ErrorCode.BORROW_005);
        }
        return LibraryConstants.STATUS_BORROWING.equals(record.getStatus())
                && LocalDateTime.now(ZoneId.of(LibraryConstants.SYSTEM_TIMEZONE))
                        .isAfter(record.getDueDate());
    }

    /**
     * DO 转 VO。
     */
    private BorrowRecordVO toBorrowRecordVO(BorrowRecordDO record, Map<Long, String> titleMap) {
        BorrowRecordVO vo = new BorrowRecordVO();
        vo.setId(record.getId());
        vo.setBookId(record.getBookId());
        vo.setBookTitle(titleMap.get(record.getBookId()));
        vo.setBorrowDate(record.getBorrowDate());
        vo.setDueDate(record.getDueDate());
        vo.setReturnDate(record.getReturnDate());
        vo.setStatus(record.getStatus());
        return vo;
    }
}

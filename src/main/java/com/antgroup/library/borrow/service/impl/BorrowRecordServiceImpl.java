package com.antgroup.library.borrow.service.impl;

import com.antgroup.library.book.entity.Book;
import com.antgroup.library.book.service.BookService;
import com.antgroup.library.borrow.dto.BorrowRecordQueryRequest;
import com.antgroup.library.borrow.dto.BorrowRecordVO;
import com.antgroup.library.borrow.dto.BorrowRequest;
import com.antgroup.library.borrow.entity.BorrowRecord;
import com.antgroup.library.borrow.enums.BorrowStatus;
import com.antgroup.library.borrow.mapper.BorrowRecordMapper;
import com.antgroup.library.borrow.service.BorrowRecordService;
import com.antgroup.library.common.exception.BizException;
import com.antgroup.library.common.exception.ErrorCode;
import com.antgroup.library.common.response.PageResult;
import com.antgroup.library.reader.entity.Reader;
import com.antgroup.library.reader.enums.ReaderStatus;
import com.antgroup.library.reader.service.ReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 借阅记录服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private static final int BORROW_LIMIT = 5;
    private static final int BORROW_PERIOD_DAYS = 30;

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookService bookService;
    private final ReaderService readerService;

    @Override
    public PageResult<BorrowRecordVO> queryRecords(BorrowRecordQueryRequest request) {
        int pageNum = request.normalizedPageNum();
        int pageSize = request.normalizedPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = borrowRecordMapper.selectCount(request.getReaderId(),
                request.getBookId(), request.getStatus());
        List<BorrowRecordVO> voList = new ArrayList<>();
        if (total == 0) {
            return PageResult.of(0, pageNum, pageSize, voList);
        }

        List<BorrowRecordVO> records = borrowRecordMapper.selectPage(offset, pageSize,
                request.getReaderId(), request.getBookId(), request.getStatus());
        voList.addAll(records);
        return PageResult.of(total, pageNum, pageSize, voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long borrowBook(BorrowRequest request) {
        Book book = bookService.getBookEntity(request.getBookId());

        Reader reader = readerService.getReaderEntity(request.getReaderId());
        if (!ReaderStatus.ACTIVE.getValue().equals(reader.getStatus())) {
            throw new BizException(ErrorCode.READER_005);
        }

        int unreturned = borrowRecordMapper.countUnreturnedByReader(request.getReaderId());
        if (unreturned >= BORROW_LIMIT) {
            throw new BizException(ErrorCode.BORROW_001);
        }

        // 幂等防重（G2.1）：同一读者对同一图书已有借阅中/逾期记录时拒绝重复借书（设计文档 R16）
        int activeBorrow = borrowRecordMapper.countActiveBorrowByReaderAndBook(
                request.getReaderId(), request.getBookId());
        if (activeBorrow > 0) {
            throw new BizException(ErrorCode.BORROW_001);
        }

        // 库存扣减为原子 SQL（stock >= #{quantity}），若返回 0 行说明库存不足（消除 TOCTOU 窗口）
        bookService.deductStock(request.getBookId(), 1);

        Date now = new Date();
        Date dueTime = computeDueTime(now);

        BorrowRecord record = new BorrowRecord();
        record.setBookId(request.getBookId());
        record.setReaderId(request.getReaderId());
        record.setBorrowTime(now);
        record.setDueTime(dueTime);
        record.setReturnTime(null);
        record.setStatus(BorrowStatus.BORROWING.getValue());
        record.setIsOverdue(0);
        borrowRecordMapper.insert(record);

        log.info("借书成功 recordId={} bookId={} readerId={} requestId={}",
                record.getId(), request.getBookId(), request.getReaderId(), request.getRequestId());
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long recordId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BizException(ErrorCode.BORROW_002);
        }
        if (!BorrowStatus.canReturn(record.getStatus())) {
            throw new BizException(ErrorCode.BORROW_003);
        }

        Date now = new Date();
        int isOverdue = now.after(record.getDueTime()) ? 1 : 0;

        int rows = borrowRecordMapper.updateToReturned(recordId, now, isOverdue);
        if (rows == 0) {
            throw new BizException(ErrorCode.BORROW_003);
        }

        bookService.restoreStock(record.getBookId(), 1);
        log.info("还书成功 recordId={} bookId={} isOverdue={}", recordId, record.getBookId(), isOverdue);
    }

    /**
     * 使用 java.time API 计算应还时间（借阅+30天），指定 Asia/Shanghai 时区避免 JVM 默认时区偏移（P1-6/P2-1）。
     */
    private Date computeDueTime(Date borrowTime) {
        ZonedDateTime zonedBorrow = borrowTime.toInstant()
                .atZone(ZoneId.of("Asia/Shanghai"));
        ZonedDateTime zonedDue = zonedBorrow.plusDays(BORROW_PERIOD_DAYS);
        return Date.from(zonedDue.toInstant());
    }
}

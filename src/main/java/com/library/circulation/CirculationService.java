package com.library.circulation;

import com.library.book.Book;
import com.library.book.BookRepository;
import com.library.circulation.dto.BorrowRecordVO;
import com.library.circulation.dto.BorrowRequest;
import com.library.circulation.dto.ReturnRequest;
import com.library.common.BusinessException;
import com.library.reader.Reader;
import com.library.reader.ReaderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 借阅流通服务：借阅、归还、逾期判定。
 * 契约对齐 circulation/spec.md (REQ-CR-001 ~ 004)。
 */
@Service
public class CirculationService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    private final int borrowLimit;
    private final int borrowPeriodDays;

    public CirculationService(BorrowRecordRepository borrowRecordRepository,
                              BookRepository bookRepository,
                              ReaderRepository readerRepository,
                              @Value("${app.circulation.borrow-limit:5}") int borrowLimit,
                              @Value("${app.circulation.borrow-period-days:30}") int borrowPeriodDays) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.borrowLimit = borrowLimit;
        this.borrowPeriodDays = borrowPeriodDays;
    }

    /**
     * REQ-CR-001：借阅。
     * 前置校验（任一失败返回 409）：读者启用、在册上限、图书存在且库存>0。
     * 动作：stock-=1（乐观锁），建借阅记录 due_at=now+30d, status=ACTIVE。
     */
    @Transactional
    public BorrowRecordVO borrow(BorrowRequest req, Long readerId) {
        // 1. 校验读者启用
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new BusinessException(404, "读者不存在"));
        if (!Boolean.TRUE.equals(reader.getEnabled())) {
            throw new BusinessException(409, "账号已禁用，无法借阅");
        }

        // 2. 校验在册上限
        long activeCount = borrowRecordRepository.countActiveByReader(
                readerId, List.of(BorrowRecordStatus.ACTIVE, BorrowRecordStatus.OVERDUE));
        if (activeCount >= borrowLimit) {
            throw new BusinessException(409, "借阅已达上限(" + borrowLimit + "本)");
        }

        // 3. 校验图书存在且库存>0
        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new BusinessException(404, "图书不存在"));
        if (book.getStock() <= 0) {
            throw new BusinessException(409, "库存不足");
        }

        // 4. 扣减库存（@Version 乐观锁，并发冲突时抛 OptimisticLockException → 500）
        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        // 5. 建借阅记录
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueAt = now.plusDays(borrowPeriodDays);
        BorrowRecord record = new BorrowRecord(book.getId(), readerId, now, dueAt);
        BorrowRecord saved = borrowRecordRepository.save(record);

        BorrowRecordVO vo = new BorrowRecordVO(saved);
        vo.setBookTitle(book.getTitle());
        vo.applyOverdue(now);
        return vo;
    }

    /**
     * REQ-CR-002：归还。
     * 前置校验：借阅记录归属当前读者、状态为 ACTIVE 或 OVERDUE。
     * 动作：stock+=1，return_at=now, status=RETURNED。
     */
    @Transactional
    public BorrowRecordVO returnBook(ReturnRequest req, Long readerId) {
        BorrowRecord record = borrowRecordRepository.findById(req.getRecordId())
                .orElseThrow(() -> new BusinessException(404, "借阅记录不存在"));

        // 校验归属
        if (!record.getReaderId().equals(readerId)) {
            throw new BusinessException(403, "无权操作他人借阅记录");
        }

        // 校验状态
        if (record.getStatus() != BorrowRecordStatus.ACTIVE
                && record.getStatus() != BorrowRecordStatus.OVERDUE) {
            throw new BusinessException(409, "该书已归还");
        }

        // 恢复库存
        Book book = bookRepository.findById(record.getBookId())
                .orElseThrow(() -> new BusinessException(404, "图书不存在"));
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);

        // 更新记录
        record.setReturnAt(LocalDateTime.now());
        record.setStatus(BorrowRecordStatus.RETURNED);
        BorrowRecord saved = borrowRecordRepository.save(record);

        BorrowRecordVO vo = new BorrowRecordVO(saved);
        vo.setBookTitle(book.getTitle());
        vo.applyOverdue(LocalDateTime.now());
        return vo;
    }

    /**
     * REQ-CR-003：查询当前读者借阅记录，含逾期状态与提示。
     * 逾期判定：now > due_at 且 status != RETURNED → 标记 OVERDUE。
     */
    @Transactional(readOnly = true)
    public List<BorrowRecordVO> myBorrowRecords(Long readerId) {
        List<BorrowRecord> records = borrowRecordRepository.findByReaderIdOrderByBorrowAtDesc(readerId);
        LocalDateTime now = LocalDateTime.now();

        // 批量查 bookId -> title
        Map<Long, String> bookTitles = records.stream()
                .map(BorrowRecord::getBookId)
                .distinct()
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> bookRepository.findById(id).map(Book::getTitle).orElse("(已删除)")
                ));

        return records.stream().map(r -> {
            BorrowRecordVO vo = new BorrowRecordVO(r);
            vo.setBookTitle(bookTitles.get(r.getBookId()));
            vo.applyOverdue(now);
            return vo;
        }).toList();
    }
}

package com.library.service.impl;

import com.library.common.BusinessConstants;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.common.ResultCode;
import com.library.dao.mapper.BookMapper;
import com.library.dao.mapper.BorrowRecordMapper;
import com.library.dao.mapper.ReaderMapper;
import com.library.model.dto.BorrowRequest;
import com.library.model.dto.BorrowVO;
import com.library.model.entity.BookDO;
import com.library.model.entity.BorrowRecordDO;
import com.library.model.entity.ReaderDO;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 借阅 Service 实现
 *
 * @author library
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final ReaderMapper readerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long borrowBook(BorrowRequest request) {
        // 1. 校验图书存在
        BookDO book = bookMapper.selectById(request.getBookId());
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND, "图书不存在: " + request.getBookId());
        }
        if (book.getStock() <= 0) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH, "图书库存不足, 当前库存: " + book.getStock());
        }

        // 2. 校验读者存在
        ReaderDO reader = readerMapper.selectById(request.getReaderId());
        if (reader == null) {
            throw new BusinessException(ResultCode.READER_NOT_FOUND, "读者不存在: " + request.getReaderId());
        }

        // 3. 校验是否重复借阅同一本未归还图书
        int activeBorrowCount = borrowRecordMapper.countActiveBorrow(request.getReaderId(), request.getBookId());
        if (activeBorrowCount > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_BORROW, "您已借阅该图书且尚未归还");
        }

        // 4. 扣减库存
        bookMapper.deductStock(request.getBookId(), 1);

        // 5. 创建借阅记录, 期限默认30天
        int borrowDays = (request.getBorrowDays() == null || request.getBorrowDays() <= 0)
                ? BusinessConstants.DEFAULT_BORROW_DAYS : request.getBorrowDays();

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, borrowDays);
        Date dueTime = cal.getTime();

        BorrowRecordDO record = new BorrowRecordDO();
        record.setBookId(request.getBookId());
        record.setReaderId(request.getReaderId());
        record.setBorrowTime(now);
        record.setDueTime(dueTime);
        record.setStatus(BusinessConstants.BORROW_STATUS_BORROWED);
        borrowRecordMapper.insert(record);

        log.info("借阅成功, recordId={}, bookId={}, readerId={}, dueTime={}",
                record.getId(), request.getBookId(), request.getReaderId(), dueTime);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowVO returnBook(Long recordId) {
        // 1. 查询借阅记录
        BorrowRecordDO record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ResultCode.BORROW_RECORD_NOT_FOUND, "借阅记录不存在: " + recordId);
        }

        // 2. 校验是否已归还
        if (BusinessConstants.BORROW_STATUS_RETURNED.equals(record.getStatus())) {
            throw new BusinessException(ResultCode.ALREADY_RETURNED, "该图书已归还, 无需重复归还");
        }

        // 3. 恢复库存
        bookMapper.restoreStock(record.getBookId(), 1);

        // 4. 判定是否逾期
        Date now = new Date();
        boolean overdue = now.after(record.getDueTime());
        String status = overdue
                ? BusinessConstants.BORROW_STATUS_OVERDUE
                : BusinessConstants.BORROW_STATUS_RETURNED;

        // 5. 更新归还记录
        borrowRecordMapper.updateReturn(recordId, now, status);

        // 6. 查询完整记录(联表)返回
        BorrowRecordDO updated = borrowRecordMapper.selectById(recordId);
        BorrowVO vo = convertToVO(updated);

        if (overdue) {
            int overdueDays = calculateOverdueDays(record.getDueTime(), now);
            vo.setOverdue(true);
            vo.setOverdueDays(overdueDays);
            log.info("归还成功(逾期), recordId={}, overdueDays={}", recordId, overdueDays);
        } else {
            vo.setOverdue(false);
            vo.setOverdueDays(0);
            log.info("归还成功(按时), recordId={}", recordId);
        }

        return vo;
    }

    @Override
    public PageResult<BorrowVO> listBorrowRecordsByReader(Long readerId, String status, int pageNum, int pageSize) {
        pageNum = Math.max(pageNum, BusinessConstants.DEFAULT_PAGE_NUM);
        pageSize = pageSize <= 0 ? BusinessConstants.DEFAULT_PAGE_SIZE : pageSize;

        long total = borrowRecordMapper.countByReader(readerId, status);
        if (total <= 0) {
            return PageResult.empty(pageNum, pageSize);
        }

        int offset = (pageNum - 1) * pageSize;
        List<BorrowRecordDO> records = borrowRecordMapper.selectPageByReader(readerId, status, offset, pageSize);
        List<BorrowVO> voList = records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageNum, pageSize, total, voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshOverdueStatus() {
        Date now = new Date();
        int rows = borrowRecordMapper.updateOverdueStatus(
                BusinessConstants.BORROW_STATUS_OVERDUE,
                BusinessConstants.BORROW_STATUS_BORROWED,
                now);
        if (rows > 0) {
            log.info("刷新逾期状态, 更新记录数: {}", rows);
        }
    }

    /**
     * DO 转 VO
     */
    private BorrowVO convertToVO(BorrowRecordDO record) {
        BorrowVO vo = new BorrowVO();
        BeanUtils.copyProperties(record, vo);
        vo.setBookTitle(record.getBookTitle());
        vo.setBookIsbn(record.getBookIsbn());
        vo.setReaderName(record.getReaderName());

        // 计算是否逾期(未归还且已超期)
        if (!BusinessConstants.BORROW_STATUS_RETURNED.equals(record.getStatus())) {
            Date now = new Date();
            boolean overdue = now.after(record.getDueTime());
            vo.setOverdue(overdue);
            vo.setOverdueDays(overdue ? calculateOverdueDays(record.getDueTime(), now) : 0);
        } else {
            vo.setOverdue(false);
            vo.setOverdueDays(0);
        }
        return vo;
    }

    /**
     * 计算逾期天数
     */
    private int calculateOverdueDays(Date dueTime, Date returnTime) {
        long diff = returnTime.getTime() - dueTime.getTime();
        if (diff <= 0) {
            return 0;
        }
        return (int) (diff / (24 * 60 * 60 * 1000L));
    }
}

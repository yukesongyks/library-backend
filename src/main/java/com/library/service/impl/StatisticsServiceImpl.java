package com.library.service.impl;

import com.library.common.BusinessConstants;
import com.library.common.PageResult;
import com.library.dao.mapper.BookMapper;
import com.library.dao.mapper.BorrowRecordMapper;
import com.library.dao.mapper.ReaderMapper;
import com.library.model.dto.BorrowVO;
import com.library.model.dto.PopularBookVO;
import com.library.model.dto.StatisticsOverviewVO;
import com.library.model.entity.BookDO;
import com.library.model.entity.BorrowRecordDO;
import com.library.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计 Service 实现
 *
 * @author library
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final BookMapper bookMapper;
    private final ReaderMapper readerMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    public StatisticsOverviewVO getOverview() {
        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        List<BookDO> allBooks = bookMapper.selectAll();

        vo.setTotalBooks((long) allBooks.size());
        vo.setTotalInStock(allBooks.stream()
                .mapToLong(BookDO::getStock)
                .sum());
        vo.setTotalBorrowed(allBooks.stream()
                .mapToLong(b -> b.getTotalStock() - b.getStock())
                .sum());
        vo.setTotalReaders((long) readerMapper.selectAll().size());
        vo.setCurrentBorrowing(borrowRecordMapper.countBorrowing());
        vo.setOverdueCount(borrowRecordMapper.countOverdue());

        return vo;
    }

    @Override
    public List<PopularBookVO> getPopularBooks(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        List<BorrowRecordDO> records = borrowRecordMapper.selectPopularBooks(limit);
        return records.stream()
                .map(this::convertToPopularVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<BorrowVO> getOverdueList(int pageNum, int pageSize) {
        pageNum = Math.max(pageNum, BusinessConstants.DEFAULT_PAGE_NUM);
        pageSize = pageSize <= 0 ? BusinessConstants.DEFAULT_PAGE_SIZE : pageSize;

        long total = borrowRecordMapper.countOverdue();
        if (total <= 0) {
            return PageResult.empty(pageNum, pageSize);
        }

        int offset = (pageNum - 1) * pageSize;
        List<BorrowRecordDO> records = borrowRecordMapper.selectOverduePage(offset, pageSize);
        List<BorrowVO> voList = records.stream()
                .map(this::convertToBorrowVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageNum, pageSize, total, voList);
    }

    /**
     * 转换为热门图书VO
     */
    private PopularBookVO convertToPopularVO(BorrowRecordDO record) {
        PopularBookVO vo = new PopularBookVO();
        vo.setBookId(record.getBookId());
        vo.setBookTitle(record.getBookTitle());
        vo.setBookIsbn(record.getBookIsbn());
        vo.setBorrowCount(record.getBorrowCount());

        BookDO book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            vo.setAuthor(book.getAuthor());
            vo.setCategory(book.getCategory());
        }
        return vo;
    }

    /**
     * 转换为借阅VO
     */
    private BorrowVO convertToBorrowVO(BorrowRecordDO record) {
        BorrowVO vo = new BorrowVO();
        BeanUtils.copyProperties(record, vo);
        vo.setBookTitle(record.getBookTitle());
        vo.setBookIsbn(record.getBookIsbn());
        vo.setReaderName(record.getReaderName());
        vo.setOverdue(true);
        vo.setOverdueDays(calculateOverdueDays(record.getDueTime()));
        return vo;
    }

    /**
     * 计算逾期天数(以当前时间为准)
     */
    private int calculateOverdueDays(Date dueTime) {
        if (dueTime == null) {
            return 0;
        }
        long diff = System.currentTimeMillis() - dueTime.getTime();
        if (diff <= 0) {
            return 0;
        }
        return (int) (diff / (24 * 60 * 60 * 1000L));
    }

    // 引入 Date 以便上面 calculateOverdueDays 使用
    private static final java.util.Date NOW_DUMMY = null;
}

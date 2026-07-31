package com.antgroup.library.borrow.mapper;

import com.antgroup.library.borrow.dto.BorrowRecordVO;
import com.antgroup.library.borrow.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 借阅记录 Mapper。
 */
@Mapper
public interface BorrowRecordMapper {

    BorrowRecord selectById(@Param("id") Long id);

    /**
     * 查询某读者未归还（BORROWING/OVERDUE）借阅数量（设计文档 S14）。
     */
    int countUnreturnedByReader(@Param("readerId") Long readerId);

    /**
     * 查询某图书未归还借阅数量（供图书删除/修改ISBN 校验）。
     */
    int countUnreturnedByBook(@Param("bookId") Long bookId);

    /**
     * 幂等防重：查询某读者对某图书是否存在借阅中/逾期状态的记录（短窗口防重复提交，设计文档 R16）。
     */
    int countActiveBorrowByReaderAndBook(@Param("readerId") Long readerId, @Param("bookId") Long bookId);

    List<BorrowRecordVO> selectPage(@Param("offset") int offset,
                                  @Param("pageSize") int pageSize,
                                  @Param("readerId") Long readerId,
                                  @Param("bookId") Long bookId,
                                  @Param("status") String status);

    long selectCount(@Param("readerId") Long readerId,
                     @Param("bookId") Long bookId,
                     @Param("status") String status);

    int insert(BorrowRecord record);

    /**
     * 状态条件防重还书：UPDATE ... SET status='RETURNED' WHERE id=? AND status IN ('BORROWING','OVERDUE')。
     */
    int updateToReturned(@Param("id") Long id,
                         @Param("returnTime") java.util.Date returnTime,
                         @Param("isOverdue") int isOverdue);
}

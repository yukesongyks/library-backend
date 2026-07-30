package com.library.circulation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    /**
     * 查询某图书的在册借阅（ACTIVE/OVERDUE），用于删除图书校验。
     */
    List<BorrowRecord> findByBookIdAndStatusIn(Long bookId, List<BorrowRecordStatus> statuses);

    /**
     * 查询某读者的在册借阅（ACTIVE/OVERDUE），用于删除读者校验。
     */
    List<BorrowRecord> findByReaderIdAndStatusIn(Long readerId, List<BorrowRecordStatus> statuses);

    /**
     * 查询某读者的在册借阅数量（ACTIVE/OVERDUE），用于借阅上限校验。
     */
    @Query("""
            SELECT COUNT(r) FROM BorrowRecord r
            WHERE r.readerId = :readerId AND r.status IN :statuses
            """)
    long countActiveByReader(@Param("readerId") Long readerId,
                              @Param("statuses") List<BorrowRecordStatus> statuses);

    /**
     * 查询某读者的所有借阅记录，按 borrowAt 降序。
     */
    List<BorrowRecord> findByReaderIdOrderByBorrowAtDesc(Long readerId);
}

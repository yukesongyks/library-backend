package com.library.dao.mapper;

import com.library.model.entity.BorrowRecordDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 借阅记录 Mapper
 *
 * @author library
 */
public interface BorrowRecordMapper {

    /**
     * 新增借阅记录
     */
    int insert(BorrowRecordDO record);

    /**
     * 更新借阅记录(归还)
     */
    int updateReturn(@Param("id") Long id,
                      @Param("returnTime") Date returnTime,
                      @Param("status") String status);

    /**
     * 更新逾期状态(批量)
     *
     * @param status   目标状态
     * @param oldStatus 原状态
     * @param now      当前时间
     */
    int updateOverdueStatus(@Param("status") String status,
                            @Param("oldStatus") String oldStatus,
                            @Param("now") Date now);

    /**
     * 根据ID查询借阅记录
     */
    BorrowRecordDO selectById(@Param("id") Long id);

    /**
     * 查询某读者未归还的借阅记录数
     *
     * @param readerId 读者ID
     */
    int countUnreturned(@Param("readerId") Long readerId);

    /**
     * 查询某读者是否已借阅某本图书且未归还
     *
     * @param readerId 读者ID
     * @param bookId   图书ID
     */
    int countActiveBorrow(@Param("readerId") Long readerId, @Param("bookId") Long bookId);

    /**
     * 查询读者借阅记录总数
     *
     * @param readerId 读者ID
     * @param status   状态(可选)
     */
    long countByReader(@Param("readerId") Long readerId, @Param("status") String status);

    /**
     * 分页查询读者借阅记录(联表图书/读者)
     *
     * @param readerId 读者ID
     * @param status   状态(可选)
     * @param offset   偏移量
     * @param pageSize 每页条数
     */
    List<BorrowRecordDO> selectPageByReader(@Param("readerId") Long readerId,
                                           @Param("status") String status,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);

    /**
     * 查询逾期记录列表(联表图书/读者)
     *
     * @param offset   偏移量
     * @param pageSize 每页条数
     */
    List<BorrowRecordDO> selectOverduePage(@Param("offset") int offset,
                                           @Param("pageSize") int pageSize);

    /**
     * 查询逾期记录总数
     */
    long countOverdue();

    /**
     * 查询借阅中记录数
     */
    long countBorrowing();

    /**
     * 查询热门图书(按借阅次数排序)
     *
     * @param limit 返回条数
     */
    List<BorrowRecordDO> selectPopularBooks(@Param("limit") int limit);
}

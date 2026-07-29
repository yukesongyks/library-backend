package com.library.borrow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.borrow.entity.BorrowRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 借阅记录 Mapper。
 *
 * @author DTCoder
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecordDO> {

    /**
     * 条件更新归还（幂等：仅 BORROWING/OVERDUE 可更新）。
     *
     * @param recordId    记录ID
     * @param userId      读者ID（水平权限校验）
     * @param status      新状态（RETURNED/OVERDUE）
     * @param returnDate  归还时间
     * @return 影响行数（0表示无权或状态不符）
     */
    int updateReturn(@Param("recordId") Long recordId,
                     @Param("userId") Long userId,
                     @Param("status") String status,
                     @Param("returnDate") java.time.LocalDateTime returnDate);

    /**
     * 统计读者在借中数量（状态=BORROWING）。
     *
     * @param userId 用户ID
     * @return 在借数量
     */
    int countBorrowing(@Param("userId") Long userId);

    /**
     * 统计读者逾期未还数量（状态=OVERDUE 或 BORROWING 且已过期）。
     *
     * @param userId 用户ID
     * @return 逾期数量
     */
    int countOverdue(@Param("userId") Long userId);

    /**
     * 分页查询读者借阅记录（可选状态筛选）。
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @param status 状态（null则不筛选）
     * @return 分页结果
     */
    IPage<BorrowRecordDO> selectByUserId(IPage<BorrowRecordDO> page,
                                         @Param("userId") Long userId,
                                         @Param("status") String status);
}

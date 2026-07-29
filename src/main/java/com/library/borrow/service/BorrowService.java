package com.library.borrow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.borrow.dto.BorrowRecordVO;
import com.library.borrow.dto.BorrowRequest;
import com.library.borrow.dto.BorrowResult;
import com.library.borrow.dto.ReturnRequest;
import com.library.borrow.dto.ReturnResult;

/**
 * 借阅管理服务接口。
 *
 * @author DTCoder
 */
public interface BorrowService {

    /**
     * 借阅图书（扣库存+生成借阅记录，事务）。
     *
     * @param req    借阅请求
     * @param userId 当前读者用户ID
     * @return 借阅结果
     */
    BorrowResult borrow(BorrowRequest req, Long userId);

    /**
     * 归还图书（恢复库存+更新记录+逾期检测，事务）。
     *
     * @param req    归还请求
     * @param userId 当前读者用户ID
     * @return 归还结果
     */
    ReturnResult returnBook(ReturnRequest req, Long userId);

    /**
     * 查询当前读者的借阅记录（水平权限：仅返回本人记录）。
     *
     * @param userId   当前读者用户ID
     * @param status   状态筛选
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<BorrowRecordVO> listMyRecords(Long userId, String status, Integer pageNum, Integer pageSize);

    /**
     * 检测指定记录是否逾期。
     *
     * @param recordId 记录ID
     * @return true=逾期
     */
    boolean isOverdue(Long recordId);
}

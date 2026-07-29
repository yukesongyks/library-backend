package com.library.service;

import com.library.common.PageResult;
import com.library.model.dto.BorrowRequest;
import com.library.model.dto.BorrowVO;

/**
 * 借阅 Service
 *
 * @author library
 */
public interface BorrowService {

    /**
     * 借阅图书
     * - 校验图书/读者存在
     * - 扣减库存
     * - 记录借阅期限(默认30天)
     *
     * @param request 借阅请求
     * @return 借阅记录ID
     */
    Long borrowBook(BorrowRequest request);

    /**
     * 归还图书
     * - 恢复库存
     * - 判定逾期并给出提示
     *
     * @param recordId 借阅记录ID
     * @return 归还结果(含逾期信息)
     */
    BorrowVO returnBook(Long recordId);

    /**
     * 查询读者借阅记录(分页)
     *
     * @param readerId 读者ID
     * @param status   状态(可选)
     * @param pageNum  页码
     * @param pageSize 每页条数
     */
    PageResult<BorrowVO> listBorrowRecordsByReader(Long readerId, String status, int pageNum, int pageSize);

    /**
     * 刷新逾期状态(将到期未还的记录标记为逾期)
     */
    void refreshOverdueStatus();
}

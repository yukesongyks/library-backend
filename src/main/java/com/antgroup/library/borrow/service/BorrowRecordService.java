package com.antgroup.library.borrow.service;

import com.antgroup.library.borrow.dto.BorrowRecordQueryRequest;
import com.antgroup.library.borrow.dto.BorrowRecordVO;
import com.antgroup.library.borrow.dto.BorrowRequest;
import com.antgroup.library.common.response.PageResult;

/**
 * 借阅记录服务（设计文档 S16-S18）。
 */
public interface BorrowRecordService {

    PageResult<BorrowRecordVO> queryRecords(BorrowRecordQueryRequest request);

    Long borrowBook(BorrowRequest request);

    void returnBook(Long recordId);
}

package com.antgroup.library.reader.service;

import com.antgroup.library.common.response.PageResult;
import com.antgroup.library.reader.dto.ReaderCreateRequest;
import com.antgroup.library.reader.dto.ReaderQueryRequest;
import com.antgroup.library.reader.dto.ReaderUpdateRequest;
import com.antgroup.library.reader.dto.ReaderVO;
import com.antgroup.library.reader.entity.Reader;

/**
 * 读者服务（设计文档 S09-S15）。
 */
public interface ReaderService {

    PageResult<ReaderVO> queryReaders(ReaderQueryRequest request);

    ReaderVO getReaderById(Long id);

    Long createReader(ReaderCreateRequest request);

    void updateReader(Long id, ReaderUpdateRequest request);

    void deleteReader(Long id);

    int countUnreturned(Long readerId);

    Reader getReaderEntity(Long id);
}

package com.library.service;

import com.library.common.PageResult;
import com.library.model.dto.ReaderRequest;
import com.library.model.dto.ReaderVO;

import java.util.List;

/**
 * 读者 Service
 *
 * @author library
 */
public interface ReaderService {

    /**
     * 分页查询读者
     *
     * @param keyword  关键词(姓名/电话)
     * @param pageNum  页码
     * @param pageSize 每页条数
     */
    PageResult<ReaderVO> searchReaders(String keyword, int pageNum, int pageSize);

    /**
     * 根据ID查询读者
     */
    ReaderVO getReaderById(Long id);

    /**
     * 查询全部读者
     */
    List<ReaderVO> listAllReaders();

    /**
     * 新增读者
     */
    Long addReader(ReaderRequest request);

    /**
     * 更新读者
     */
    void updateReader(Long id, ReaderRequest request);

    /**
     * 删除读者
     */
    void deleteReader(Long id);
}

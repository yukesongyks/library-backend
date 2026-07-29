package com.library.reader.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.reader.dto.ReaderCreateRequest;
import com.library.reader.dto.ReaderUpdateRequest;
import com.library.reader.dto.ReaderVO;
import com.library.reader.entity.ReaderDO;

/**
 * 读者管理服务接口。
 *
 * @author DTCoder
 */
public interface ReaderService {

    /**
     * 新增读者（同时创建sys_user和reader，事务）。
     *
     * @param req 新增请求
     * @return 新建读者ID
     */
    Long createReader(ReaderCreateRequest req);

    /**
     * 删除读者（逻辑删除）。
     *
     * @param id 读者ID
     */
    void deleteReader(Long id);

    /**
     * 修改读者。
     *
     * @param id  读者ID
     * @param req 修改请求
     */
    void updateReader(Long id, ReaderUpdateRequest req);

    /**
     * 分页查询读者。
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<ReaderVO> pageReaders(Integer pageNum, Integer pageSize);

    /**
     * 根据用户ID查询读者。
     *
     * @param userId sys_user.id
     * @return 读者记录
     */
    ReaderDO getByUserId(Long userId);
}

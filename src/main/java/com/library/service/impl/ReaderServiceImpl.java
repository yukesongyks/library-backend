package com.library.service.impl;

import com.library.common.BusinessConstants;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.common.ResultCode;
import com.library.dao.mapper.ReaderMapper;
import com.library.model.dto.ReaderRequest;
import com.library.model.dto.ReaderVO;
import com.library.model.entity.ReaderDO;
import com.library.service.ReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 读者 Service 实现
 *
 * @author library
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final ReaderMapper readerMapper;

    @Override
    public PageResult<ReaderVO> searchReaders(String keyword, int pageNum, int pageSize) {
        pageNum = Math.max(pageNum, BusinessConstants.DEFAULT_PAGE_NUM);
        pageSize = pageSize <= 0 ? BusinessConstants.DEFAULT_PAGE_SIZE : pageSize;

        long total = readerMapper.selectCount(keyword);
        if (total <= 0) {
            return PageResult.empty(pageNum, pageSize);
        }

        int offset = (pageNum - 1) * pageSize;
        List<ReaderDO> readerList = readerMapper.selectPage(keyword, offset, pageSize);
        List<ReaderVO> voList = readerList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageNum, pageSize, total, voList);
    }

    @Override
    public ReaderVO getReaderById(Long id) {
        ReaderDO reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException(ResultCode.READER_NOT_FOUND, "读者不存在: " + id);
        }
        return convertToVO(reader);
    }

    @Override
    public List<ReaderVO> listAllReaders() {
        return readerMapper.selectAll().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addReader(ReaderRequest request) {
        ReaderDO reader = new ReaderDO();
        BeanUtils.copyProperties(request, reader);
        readerMapper.insert(reader);
        log.info("新增读者成功, id={}, name={}", reader.getId(), reader.getName());
        return reader.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReader(Long id, ReaderRequest request) {
        ReaderDO reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException(ResultCode.READER_NOT_FOUND, "读者不存在: " + id);
        }

        ReaderDO updateDO = new ReaderDO();
        updateDO.setId(id);
        BeanUtils.copyProperties(request, updateDO);
        readerMapper.update(updateDO);
        log.info("更新读者成功, id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReader(Long id) {
        ReaderDO reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException(ResultCode.READER_NOT_FOUND, "读者不存在: " + id);
        }
        readerMapper.deleteById(id);
        log.info("删除读者成功, id={}", id);
    }

    /**
     * DO 转 VO
     */
    private ReaderVO convertToVO(ReaderDO reader) {
        ReaderVO vo = new ReaderVO();
        BeanUtils.copyProperties(reader, vo);
        return vo;
    }
}

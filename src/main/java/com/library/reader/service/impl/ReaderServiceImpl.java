package com.library.reader.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.auth.service.AuthService;
import com.library.common.constant.LibraryConstants;
import com.library.common.exception.BizException;
import com.library.common.exception.ErrorCode;
import com.library.reader.dto.ReaderCreateRequest;
import com.library.reader.dto.ReaderUpdateRequest;
import com.library.reader.dto.ReaderVO;
import com.library.reader.entity.ReaderDO;
import com.library.reader.mapper.ReaderMapper;
import com.library.reader.service.ReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 读者管理服务实现。
 *
 * @author DTCoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final ReaderMapper readerMapper;
    private final AuthService authService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReader(ReaderCreateRequest req) {
        // R01: username 全局唯一
        if (authService.existsByUsername(req.getUsername())) {
            throw new BizException(ErrorCode.READER_001);
        }

        // R02: readerNo 全局唯一
        if (readerMapper.selectByReaderNo(req.getReaderNo()) != null) {
            throw new BizException(ErrorCode.READER_002);
        }

        // R03: 事务中同时创建 sys_user 和 reader
        Long userId = authService.createSysUser(
                req.getUsername(), req.getPassword(), LibraryConstants.ROLE_READER);

        ReaderDO reader = new ReaderDO();
        reader.setUserId(userId);
        reader.setReaderNo(req.getReaderNo());
        reader.setPhone(req.getPhone());
        reader.setName(req.getName());
        readerMapper.insert(reader);
        log.info("新增读者成功: id={}, userId={}, readerNo={}", reader.getId(), userId, reader.getReaderNo());
        return reader.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReader(Long id) {
        ReaderDO reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BizException(ErrorCode.READER_003);
        }
        readerMapper.deleteById(id);
        log.info("删除读者成功: id={}", id);
    }

    @Override
    public void updateReader(Long id, ReaderUpdateRequest req) {
        ReaderDO reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BizException(ErrorCode.READER_003);
        }
        if (req.getPhone() != null) {
            reader.setPhone(req.getPhone());
        }
        if (req.getName() != null) {
            reader.setName(req.getName());
        }
        readerMapper.updateById(reader);
        log.info("修改读者成功: id={}", id);
    }

    @Override
    public IPage<ReaderVO> pageReaders(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ReaderDO> page = new Page<>(pageNum, pageSize);
        IPage<ReaderDO> readerPage = readerMapper.selectPage(page, null);
        return readerPage.convert(this::toReaderVO);
    }

    @Override
    public ReaderDO getByUserId(Long userId) {
        return readerMapper.selectByUserId(userId);
    }

    /**
     * DO 转 VO（手机号脱敏）。
     */
    private ReaderVO toReaderVO(ReaderDO reader) {
        ReaderVO vo = new ReaderVO();
        vo.setId(reader.getId());
        vo.setUserId(reader.getUserId());
        vo.setReaderNo(reader.getReaderNo());
        vo.setPhone(desensitizePhone(reader.getPhone()));
        vo.setName(reader.getName());
        return vo;
    }

    /**
     * 手机号脱敏：如 13800138000 → 138****8000。
     */
    private String desensitizePhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}

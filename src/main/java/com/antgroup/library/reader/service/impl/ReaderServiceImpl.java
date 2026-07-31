package com.antgroup.library.reader.service.impl;

import com.antgroup.library.borrow.mapper.BorrowRecordMapper;
import com.antgroup.library.common.exception.BizException;
import com.antgroup.library.common.exception.ErrorCode;
import com.antgroup.library.common.response.PageResult;
import com.antgroup.library.reader.dto.ReaderCreateRequest;
import com.antgroup.library.reader.dto.ReaderQueryRequest;
import com.antgroup.library.reader.dto.ReaderUpdateRequest;
import com.antgroup.library.reader.dto.ReaderVO;
import com.antgroup.library.reader.entity.Reader;
import com.antgroup.library.reader.enums.ReaderStatus;
import com.antgroup.library.reader.mapper.ReaderMapper;
import com.antgroup.library.reader.service.ReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 读者服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final ReaderMapper readerMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    public PageResult<ReaderVO> queryReaders(ReaderQueryRequest request) {
        int pageNum = request.normalizedPageNum();
        int pageSize = request.normalizedPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = readerMapper.selectCount(request.getName(), request.getPhone());
        List<ReaderVO> voList = new ArrayList<>();
        if (total == 0) {
            return PageResult.of(0, pageNum, pageSize, voList);
        }

        List<Reader> readers = readerMapper.selectPage(offset, pageSize, request.getName(), request.getPhone());
        for (Reader reader : readers) {
            voList.add(convertToVO(reader));
        }
        return PageResult.of(total, pageNum, pageSize, voList);
    }

    @Override
    public ReaderVO getReaderById(Long id) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BizException(ErrorCode.READER_001);
        }
        return convertToVO(reader);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReader(ReaderCreateRequest request) {
        Reader exists = readerMapper.selectByPhone(request.getPhone());
        if (exists != null) {
            throw new BizException(ErrorCode.READER_002);
        }
        Reader reader = new Reader();
        reader.setName(request.getName());
        reader.setPhone(request.getPhone());
        reader.setReaderType(request.getReaderType());
        reader.setStatus(ReaderStatus.ACTIVE.getValue());
        readerMapper.insert(reader);
        return reader.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReader(Long id, ReaderUpdateRequest request) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BizException(ErrorCode.READER_001);
        }
        if (request.getPhone() != null && !request.getPhone().equals(reader.getPhone())) {
            Reader phoneExists = readerMapper.selectByPhone(request.getPhone());
            if (phoneExists != null && !phoneExists.getId().equals(id)) {
                throw new BizException(ErrorCode.READER_002);
            }
        }
        if (request.getStatus() != null && !ReaderStatus.isValid(request.getStatus())) {
            throw new BizException(ErrorCode.READER_003);
        }
        Reader update = new Reader();
        update.setId(id);
        update.setName(request.getName());
        update.setPhone(request.getPhone());
        update.setReaderType(request.getReaderType());
        update.setStatus(request.getStatus());
        int rows = readerMapper.update(update);
        if (rows == 0) {
            throw new BizException(ErrorCode.READER_001);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReader(Long id) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BizException(ErrorCode.READER_001);
        }
        int unreturned = borrowRecordMapper.countUnreturnedByReader(id);
        if (unreturned > 0) {
            throw new BizException(ErrorCode.READER_004);
        }
        readerMapper.logicDelete(id);
    }

    @Override
    public int countUnreturned(Long readerId) {
        return borrowRecordMapper.countUnreturnedByReader(readerId);
    }

    @Override
    public Reader getReaderEntity(Long id) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BizException(ErrorCode.READER_001);
        }
        return reader;
    }

    private ReaderVO convertToVO(Reader reader) {
        ReaderVO vo = new ReaderVO();
        vo.setId(reader.getId());
        vo.setName(reader.getName());
        vo.setPhone(maskPhone(reader.getPhone()));
        vo.setReaderType(reader.getReaderType());
        vo.setStatus(reader.getStatus());
        return vo;
    }

    /**
     * 手机号脱敏：中间4位以*替代（设计文档 6.4.3.2）。
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}

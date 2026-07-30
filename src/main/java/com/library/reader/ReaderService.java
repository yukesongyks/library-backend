package com.library.reader;

import com.library.auth.Role;
import com.library.common.BusinessException;
import com.library.reader.dto.ReaderDTO;
import com.library.reader.dto.ReaderVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final PasswordEncoder passwordEncoder;

    public ReaderService(ReaderRepository readerRepository, PasswordEncoder passwordEncoder) {
        this.readerRepository = readerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 新增读者/管理员。username 重复返回 409。
     */
    @Transactional
    public ReaderVO create(ReaderDTO dto) {
        if (readerRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException(409, "用户名已存在: " + dto.getUsername());
        }
        Reader reader = new Reader(
                dto.getName(),
                dto.getUsername(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getRole() != null ? dto.getRole() : Role.READER
        );
        if (dto.getEnabled() != null) {
            reader.setEnabled(dto.getEnabled());
        }
        Reader saved = readerRepository.save(reader);
        return new ReaderVO(saved);
    }

    /**
     * 更新读者。password 为空则不修改密码。
     */
    @Transactional
    public ReaderVO update(Long id, ReaderDTO dto) {
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "读者不存在"));
        // username 变更时校验唯一
        if (!reader.getUsername().equals(dto.getUsername()) && readerRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException(409, "用户名已存在: " + dto.getUsername());
        }
        reader.setName(dto.getName());
        reader.setUsername(dto.getUsername());
        if (dto.getRole() != null) {
            reader.setRole(dto.getRole());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            reader.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getEnabled() != null) {
            reader.setEnabled(dto.getEnabled());
        }
        Reader saved = readerRepository.save(reader);
        return new ReaderVO(saved);
    }

    @Transactional(readOnly = true)
    public Reader getById(Long id) {
        return readerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "读者不存在"));
    }

    @Transactional(readOnly = true)
    public Page<Reader> findByKeyword(String keyword, Pageable pageable) {
        // 简单实现：用 repository 没有关键词方法时，用 findAll
        // 为精确实现，这里改用自定义查询
        return readerRepository.findByKeyword(keyword, pageable);
    }
}

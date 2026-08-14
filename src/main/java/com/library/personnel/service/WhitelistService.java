package com.library.personnel.service;

import com.library.personnel.dto.request.WhitelistRequest;
import com.library.personnel.dto.response.WhitelistResponse;
import com.library.personnel.entity.Whitelist;
import com.library.personnel.enums.WhitelistType;
import com.library.personnel.exception.BusinessException;
import com.library.personnel.repository.WhitelistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WhitelistService {

    private final WhitelistRepository whitelistRepository;

    public WhitelistService(WhitelistRepository whitelistRepository) {
        this.whitelistRepository = whitelistRepository;
    }

    public List<WhitelistResponse> listWhitelist(WhitelistType type) {
        List<Whitelist> whitelists;
        if (type != null) {
            whitelists = whitelistRepository.findByType(type);
        } else {
            whitelists = whitelistRepository.findAll();
        }
        return whitelists.stream().map(WhitelistResponse::fromEntity).toList();
    }

    @Transactional
    public WhitelistResponse addToWhitelist(WhitelistRequest request) {
        if (whitelistRepository.existsByEmployeeIdAndType(request.getEmployeeId(), request.getType())) {
            throw new BusinessException(40001,
                "Employee " + request.getEmployeeId() + " is already in the " + request.getType() + " whitelist");
        }

        Whitelist whitelist = new Whitelist();
        whitelist.setType(request.getType());
        whitelist.setEmployeeId(request.getEmployeeId());
        whitelist.setName(request.getName());
        whitelist.setNote(request.getNote());
        whitelist = whitelistRepository.save(whitelist);
        return WhitelistResponse.fromEntity(whitelist);
    }

    @Transactional
    public List<WhitelistResponse> batchAddToWhitelist(List<WhitelistRequest> requests) {
        return requests.stream().map(this::addToWhitelist).toList();
    }

    @Transactional
    public void removeFromWhitelist(Long id) {
        if (!whitelistRepository.existsById(id)) {
            throw new BusinessException(40001, "Whitelist entry not found with id: " + id);
        }
        whitelistRepository.deleteById(id);
    }

    public boolean checkWhitelist(String employeeId, WhitelistType type) {
        return whitelistRepository.existsByEmployeeIdAndType(employeeId, type);
    }
}
package com.library.personnel.repository;

import com.library.personnel.entity.Whitelist;
import com.library.personnel.enums.WhitelistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, Long> {

    List<Whitelist> findByType(WhitelistType type);

    List<Whitelist> findByEmployeeId(String employeeId);

    Optional<Whitelist> findByEmployeeIdAndType(String employeeId, WhitelistType type);

    boolean existsByEmployeeIdAndType(String employeeId, WhitelistType type);

    void deleteByEmployeeIdAndType(String employeeId, WhitelistType type);
}
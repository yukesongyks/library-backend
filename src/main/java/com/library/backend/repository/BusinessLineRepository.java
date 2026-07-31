package com.library.backend.repository;

import com.library.backend.entity.BusinessLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessLineRepository extends JpaRepository<BusinessLine, Long> {
}

package com.library.backend.repository;

import com.library.backend.entity.ProjectBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectBudgetRepository extends JpaRepository<ProjectBudget, Long> {
    List<ProjectBudget> findByBudgetYear(Integer budgetYear);
}

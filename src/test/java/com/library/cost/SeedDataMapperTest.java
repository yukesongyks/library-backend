package com.library.cost;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.cost.entity.Department;
import com.library.cost.entity.LaborCost;
import com.library.cost.entity.ProjectCost;
import com.library.cost.mapper.DepartmentMapper;
import com.library.cost.mapper.LaborCostMapper;
import com.library.cost.mapper.ProjectCostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeedDataMapperTest {

    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private LaborCostMapper laborCostMapper;
    @Autowired
    private ProjectCostMapper projectCostMapper;

    @Test
    void seedDataLoaded() {
        assertThat(departmentMapper.selectList(null)).hasSize(2);
        List<LaborCost> laborCosts = laborCostMapper.selectList(new QueryWrapper<>());
        assertThat(laborCosts).hasSize(24);
        BigDecimal totalLabor = laborCosts.stream()
                .map(LaborCost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalLabor).isEqualByComparingTo("414000.00");

        List<ProjectCost> projectCosts = projectCostMapper.selectList(new QueryWrapper<>());
        assertThat(projectCosts).hasSize(24);
        BigDecimal totalActual = projectCosts.stream()
                .map(ProjectCost::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalActual).isEqualByComparingTo("1590000.00");
    }
}
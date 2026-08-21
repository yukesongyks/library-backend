package com.library.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.cost.entity.ProjectCost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectCostMapper extends BaseMapper<ProjectCost> {
}
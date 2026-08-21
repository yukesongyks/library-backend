package com.library.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.cost.entity.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
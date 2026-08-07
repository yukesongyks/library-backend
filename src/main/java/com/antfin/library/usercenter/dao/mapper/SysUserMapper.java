package com.antfin.library.usercenter.dao.mapper;

import com.antfin.library.usercenter.dao.entity.SysUserEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户 Mapper
 */
public interface SysUserMapper {

    /**
     * 根据用户ID查询用户信息
     */
    SysUserEntity selectByUserId(@Param("userId") String userId);
}

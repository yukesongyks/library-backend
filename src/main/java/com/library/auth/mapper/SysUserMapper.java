package com.library.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.auth.entity.SysUserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户 Mapper。
 *
 * @author DTCoder
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserDO> {

    /**
     * 根据用户名查询用户。
     *
     * @param username 用户名
     * @return 用户记录
     */
    SysUserDO selectByUsername(@Param("username") String username);
}

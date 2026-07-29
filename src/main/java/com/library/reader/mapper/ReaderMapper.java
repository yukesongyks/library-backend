package com.library.reader.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.reader.entity.ReaderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 读者 Mapper。
 *
 * @author DTCoder
 */
@Mapper
public interface ReaderMapper extends BaseMapper<ReaderDO> {

    /**
     * 根据用户ID查询读者。
     *
     * @param userId sys_user.id
     * @return 读者记录
     */
    ReaderDO selectByUserId(@Param("userId") Long userId);

    /**
     * 根据读者编号查询读者。
     *
     * @param readerNo 读者编号
     * @return 读者记录
     */
    ReaderDO selectByReaderNo(@Param("readerNo") String readerNo);
}

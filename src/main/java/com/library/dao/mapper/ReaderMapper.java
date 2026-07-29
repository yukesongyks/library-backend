package com.library.dao.mapper;

import com.library.model.entity.ReaderDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 读者 Mapper
 *
 * @author library
 */
public interface ReaderMapper {

    /**
     * 新增读者
     */
    int insert(ReaderDO reader);

    /**
     * 更新读者
     */
    int update(ReaderDO reader);

    /**
     * 根据ID删除读者
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询读者
     */
    ReaderDO selectById(@Param("id") Long id);

    /**
     * 查询读者总数
     *
     * @param keyword 关键词(姓名/电话)
     */
    long selectCount(@Param("keyword") String keyword);

    /**
     * 分页查询读者
     *
     * @param keyword  关键词(姓名/电话)
     * @param offset   偏移量
     * @param pageSize 每页条数
     */
    List<ReaderDO> selectPage(@Param("keyword") String keyword,
                              @Param("offset") int offset,
                              @Param("pageSize") int pageSize);

    /**
     * 查询全部读者
     */
    List<ReaderDO> selectAll();
}

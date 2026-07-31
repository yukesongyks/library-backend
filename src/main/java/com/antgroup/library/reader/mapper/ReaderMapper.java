package com.antgroup.library.reader.mapper;

import com.antgroup.library.reader.entity.Reader;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 读者 Mapper。
 */
@Mapper
public interface ReaderMapper {

    Reader selectById(@Param("id") Long id);

    Reader selectByPhone(@Param("phone") String phone);

    List<Reader> selectPage(@Param("offset") int offset,
                            @Param("pageSize") int pageSize,
                            @Param("name") String name,
                            @Param("phone") String phone);

    long selectCount(@Param("name") String name, @Param("phone") String phone);

    int insert(Reader reader);

    int update(Reader reader);

    int logicDelete(@Param("id") Long id);
}

package com.library.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.book.entity.BookCategoryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书分类 Mapper。
 *
 * @author DTCoder
 */
@Mapper
public interface BookCategoryMapper extends BaseMapper<BookCategoryDO> {
}

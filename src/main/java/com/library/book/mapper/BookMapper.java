package com.library.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.book.entity.BookDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书 Mapper。
 *
 * @author DTCoder
 */
@Mapper
public interface BookMapper extends BaseMapper<BookDO> {

    /**
     * 扣减库存（行级锁防超卖）。
     * <p>
     * UPDATE book SET stock = stock - #{qty} WHERE id = #{bookId} AND stock >= #{qty}
     * </p>
     *
     * @param bookId 图书ID
     * @param qty    扣减数量
     * @return 影响行数（0表示库存不足）
     */
    int deductStock(@Param("bookId") Long bookId, @Param("qty") int qty);

    /**
     * 恢复库存。
     * <p>
     * UPDATE book SET stock = stock + #{qty} WHERE id = #{bookId}
     * </p>
     *
     * @param bookId 图书ID
     * @param qty    恢复数量
     * @return 影响行数
     */
    int restoreStock(@Param("bookId") Long bookId, @Param("qty") int qty);

    /**
     * 读者搜索图书（分页，keyword 模糊匹配 title 或 author，可选 categoryId）。
     *
     * @param page       分页参数
     * @param keyword    搜索关键词（null或空则不过滤）
     * @param categoryId 分类ID（null则不过滤）
     * @return 分页结果
     */
    IPage<BookDO> searchBooks(IPage<BookDO> page,
                              @Param("keyword") String keyword,
                              @Param("categoryId") Long categoryId);
}

package com.library.dao.mapper;

import com.library.model.entity.BookDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书 Mapper
 *
 * @author library
 */
public interface BookMapper {

    /**
     * 新增图书
     */
    int insert(BookDO book);

    /**
     * 更新图书
     */
    int update(BookDO book);

    /**
     * 根据ID删除图书
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询图书
     */
    BookDO selectById(@Param("id") Long id);

    /**
     * 根据ISBN查询图书
     */
    BookDO selectByIsbn(@Param("isbn") String isbn);

    /**
     * 条件查询图书总数
     *
     * @param keyword  关键词(书名/作者)
     * @param category 分类
     */
    long selectCount(@Param("keyword") String keyword, @Param("category") String category);

    /**
     * 条件分页查询图书
     *
     * @param keyword  关键词(书名/作者)
     * @param category 分类
     * @param offset   偏移量
     * @param pageSize 每页条数
     */
    List<BookDO> selectPage(@Param("keyword") String keyword,
                            @Param("category") String category,
                            @Param("offset") int offset,
                            @Param("pageSize") int pageSize);

    /**
     * 查询全部图书(统计/下拉用)
     */
    List<BookDO> selectAll();

    /**
     * 扣减库存(借阅时使用)
     *
     * @param bookId 图书ID
     * @param count  扣减数量
     * @return 影响行数
     */
    int deductStock(@Param("bookId") Long bookId, @Param("count") int count);

    /**
     * 恢复库存(归还时使用)
     *
     * @param bookId 图书ID
     * @param count  恢复数量
     * @return 影响行数
     */
    int restoreStock(@Param("bookId") Long bookId, @Param("count") int count);
}

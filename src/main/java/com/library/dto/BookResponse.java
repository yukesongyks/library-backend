package com.library.dto;

import com.library.entity.Book;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书响应DTO
 *
 * @author library-team
 */
@Data
public class BookResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * ISBN编号
     */
    private String isbn;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 实体转DTO
     */
    public static BookResponse of(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setPublisher(book.getPublisher());
        response.setStock(book.getStock());
        response.setCreateTime(book.getCreateTime());
        response.setUpdateTime(book.getUpdateTime());
        return response;
    }
}

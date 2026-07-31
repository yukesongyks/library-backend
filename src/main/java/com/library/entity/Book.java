package com.library.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书实体
 *
 * @author library-team
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(name = "snowflakeIdGenerator", strategy = "com.library.common.generator.SnowflakeIdGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 书名
     */
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    /**
     * 作者
     */
    @Column(name = "author", length = 100, nullable = false)
    private String author;

    /**
     * ISBN
     */
    @Column(name = "isbn", length = 20, nullable = false, unique = true)
    private String isbn;

    /**
     * 出版社
     */
    @Column(name = "publisher", length = 200)
    private String publisher;

    /**
     * 库存数量
     */
    @Column(name = "stock", nullable = false)
    private Integer stock;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}

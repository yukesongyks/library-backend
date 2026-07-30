package com.library.book.dto;

import com.library.book.Book;

import java.time.LocalDateTime;

/**
 * 图书详情响应 DTO。
 */
public class BookVO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookVO() {
    }

    public BookVO(Book b) {
        this.id = b.getId();
        this.title = b.getTitle();
        this.author = b.getAuthor();
        this.isbn = b.getIsbn();
        this.category = b.getCategory();
        this.stock = b.getStock();
        this.createdAt = b.getCreatedAt();
        this.updatedAt = b.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

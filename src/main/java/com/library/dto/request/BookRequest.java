package com.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookRequest {

    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名长度不能超过200")
    private String title;

    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过100")
    private String author;

    @NotBlank(message = "ISBN不能为空")
    @Size(min = 10, max = 20, message = "ISBN长度必须在10-20之间")
    private String isbn;

    @Size(max = 100, message = "出版社长度不能超过100")
    private String publisher;

    @Size(max = 50, message = "分类长度不能超过50")
    private String category;

    @NotNull(message = "总库存不能为空")
    @Min(value = 0, message = "总库存必须大于等于0")
    private Integer totalStock;

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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }
}

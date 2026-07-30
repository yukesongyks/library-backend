package com.library.circulation.dto;

import jakarta.validation.constraints.NotNull;

public class BorrowRequest {

    @NotNull(message = "bookId不能为空")
    private Long bookId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}

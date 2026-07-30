package com.library.circulation.dto;

import com.library.circulation.BorrowRecord;
import com.library.circulation.BorrowRecordStatus;

import java.time.LocalDateTime;

/**
 * 借阅记录响应 VO，含逾期提示。
 */
public class BorrowRecordVO {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long readerId;
    private LocalDateTime borrowAt;
    private LocalDateTime dueAt;
    private LocalDateTime returnAt;
    private String status;
    private Boolean overdue;
    private String overdueTip;

    public BorrowRecordVO() {
    }

    public BorrowRecordVO(BorrowRecord r) {
        this.id = r.getId();
        this.bookId = r.getBookId();
        this.readerId = r.getReaderId();
        this.borrowAt = r.getBorrowAt();
        this.dueAt = r.getDueAt();
        this.returnAt = r.getReturnAt();
        this.status = r.getStatus().name();
    }

    /**
     * 填充逾期状态与提示。由 Service 调用。
     */
    public void applyOverdue(LocalDateTime now) {
        if (RETURNED.equals(this.status)) {
            this.overdue = false;
            this.overdueTip = null;
            return;
        }
        boolean isOverdue = now.isAfter(this.dueAt);
        this.overdue = isOverdue;
        this.overdueTip = isOverdue ? "已逾期，请尽快归还" : null;
        if (isOverdue) {
            this.status = BorrowRecordStatus.OVERDUE.name();
        }
    }

    private static final String RETURNED = "RETURNED";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public LocalDateTime getBorrowAt() {
        return borrowAt;
    }

    public void setBorrowAt(LocalDateTime borrowAt) {
        this.borrowAt = borrowAt;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public LocalDateTime getReturnAt() {
        return returnAt;
    }

    public void setReturnAt(LocalDateTime returnAt) {
        this.returnAt = returnAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getOverdue() {
        return overdue;
    }

    public void setOverdue(Boolean overdue) {
        this.overdue = overdue;
    }

    public String getOverdueTip() {
        return overdueTip;
    }

    public void setOverdueTip(String overdueTip) {
        this.overdueTip = overdueTip;
    }
}

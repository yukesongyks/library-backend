package com.example.library.service;

public class ExportResult {

    private byte[] content;
    private String contentType;
    private String filename;

    public ExportResult() {
    }

    public ExportResult(byte[] content, String contentType, String filename) {
        this.content = content;
        this.contentType = contentType;
        this.filename = filename;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
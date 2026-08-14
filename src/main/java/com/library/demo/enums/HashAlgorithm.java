package com.library.demo.enums;

public enum HashAlgorithm {
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA512("SHA-512");

    private final String javaName;

    HashAlgorithm(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaName() {
        return javaName;
    }
}

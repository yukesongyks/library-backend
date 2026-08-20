package com.library.demo.service;

import com.library.demo.model.response.HashResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashServiceTest {

    private final HashService service = new HashService();

    @Test
    void hash_sha256_returnsCorrectHash() {
        HashResponse response = service.hash("hello", "SHA-256");
        assertEquals("hello", response.getInput());
        assertEquals("SHA-256", response.getAlgorithm());
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", response.getHashValue());
    }

    @Test
    void hash_md5_returnsCorrectHash() {
        HashResponse response = service.hash("hello", "MD5");
        assertEquals("5d41402abc4b2a76b9719d911017c592", response.getHashValue());
    }

    @Test
    void hash_defaultAlgorithm_usesSha256() {
        HashResponse response = service.hash("test", null);
        assertEquals("SHA-256", response.getAlgorithm());
    }

    @Test
    void hash_unsupportedAlgorithm_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.hash("test", "BCRYPT"));
    }
}

package com.example.library.service;

import com.example.library.dto.HashResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Service for computing SHA-256 hash of input strings.
 */
@Service
public class HashService {

    private static final Logger log = LoggerFactory.getLogger(HashService.class);

    /**
     * Computes the SHA-256 hash of the given input string.
     *
     * @param input     the string to hash (must not be blank)
     * @param algorithm the hash algorithm (must be "SHA-256")
     * @return HashResponse containing input, algorithm, and hash value
     * @throws IllegalArgumentException if input is blank or algorithm is unsupported
     */
    public HashResponse computeHash(String input, String algorithm) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input must not be empty");
        }
        if (algorithm == null || !algorithm.equals("SHA-256")) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            String hash = bytesToHex(digest);
            return new HashResponse(input, algorithm, hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Unsupported algorithm: {}", algorithm, e);
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm, e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
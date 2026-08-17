package com.example.library.service;

import com.example.library.dto.HashResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

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
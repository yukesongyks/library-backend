package com.library.demo.service;

import com.library.demo.model.response.HashResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class HashService {

    private static final Set<String> SUPPORTED = Set.of("MD5", "SHA-1", "SHA-256");

    public HashResponse hash(String input, String algorithm) {
        String algo = (algorithm == null || algorithm.isBlank()) ? "SHA-256" : algorithm.toUpperCase();
        if (!SUPPORTED.contains(algo)) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algo + ". Supported: MD5, SHA-1, SHA-256");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algo);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String hashValue = bytesToHex(hashBytes);
            return new HashResponse(input, algo, hashValue, LocalDateTime.now());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not available: " + algo, e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

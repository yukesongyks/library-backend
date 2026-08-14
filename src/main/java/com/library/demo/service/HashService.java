package com.library.demo.service;

import com.library.demo.dto.request.HashRequest;
import com.library.demo.dto.response.HashResult;
import com.library.demo.enums.HashAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
@Slf4j
public class HashService {

    public HashResult hash(HashRequest request) {
        long start = System.currentTimeMillis();

        String algorithmStr = (request.getAlgorithm() != null && !request.getAlgorithm().isBlank())
                ? request.getAlgorithm() : "SHA256";
        HashAlgorithm algorithm = HashAlgorithm.valueOf(algorithmStr);

        String hashValue = computeHash(request.getInput(), algorithm);
        long elapsed = System.currentTimeMillis() - start;

        return new HashResult(
                request.getInput(),
                algorithmStr,
                hashValue,
                Instant.now().toString(),
                elapsed
        );
    }

    private String computeHash(String input, HashAlgorithm algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm.getJavaName());
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Unsupported hash algorithm: {}", algorithm, e);
            throw new IllegalStateException("Unsupported hash algorithm: " + algorithm, e);
        }
    }
}

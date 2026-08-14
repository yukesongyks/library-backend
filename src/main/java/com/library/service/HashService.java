package com.library.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HashService {

    public String compute(String input, String algorithm) {
        String javaAlgo = algorithm.replace("-", "-");
        try {
            MessageDigest md = MessageDigest.getInstance(javaAlgo);
            byte[] digest = md.digest(input.getBytes());
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("不支持的算法: " + algorithm);
        }
    }
}
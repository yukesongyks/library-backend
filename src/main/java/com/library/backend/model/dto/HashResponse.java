package com.library.backend.model.dto;

/**
 * Hash response data (design spec §4.3).
 *
 * @param original  original input text
 * @param algorithm fixed "SHA-256"
 * @param digest    64-char lowercase hex digest
 */
public record HashResponse(String original, String algorithm, String digest) {
}

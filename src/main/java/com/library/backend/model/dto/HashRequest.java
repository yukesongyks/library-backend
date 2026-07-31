package com.library.backend.model.dto;

/**
 * Hash request body (design spec §4.3).
 *
 * <p>Per-interface validation (§5.2.2) is performed as a business precheck in the
 * service layer (throws {@code IllegalArgumentException} → code 40002):
 * <ul>
 *   <li>empty {@code text} → 40002 "text 不能为空"</li>
 *   <li>{@code text.length() > 1000} → 40002 "text 长度超限(≤1000)"</li>
 * </ul>
 *
 * @param text text to be hashed
 */
public record HashRequest(String text) {
}

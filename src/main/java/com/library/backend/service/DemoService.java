package com.library.backend.service;

import com.library.backend.model.dto.BubbleSortResponse;
import com.library.backend.model.dto.HashResponse;
import com.library.backend.model.dto.HelloWorldResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Demo business service (design spec §4.2–§4.4, §5.2.2, §5.2.3).
 *
 * <p>Holds the core algorithms: a hand-written bubble sort that counts adjacent
 * swaps, and a SHA-256 hasher built on the JDK {@link MessageDigest}.
 */
@Service
public class DemoService {

    /** Fixed greeting (§4.2). */
    public static final String HELLO_WORLD_MESSAGE = "Hello, World!";

    /** Fixed hash algorithm identifier (§4.3). */
    public static final String HASH_ALGORITHM = "SHA-256";

    /** Max text length for the hash endpoint (§5.2.2). */
    public static final int MAX_TEXT_LENGTH = 1000;

    /** Max array length for the bubble-sort endpoint (§7). */
    public static final int MAX_ARRAY_LENGTH = 1000;

    /**
     * Default sample text used by the export endpoint when there is no session
     * input (§4.5 documentation requirement). Export always returns content.
     */
    public static final String DEFAULT_HASH_SAMPLE_TEXT = "Hello, World!";

    /**
     * Default sample array used by the export endpoint when there is no session
     * input (§4.5 documentation requirement). Export always returns content.
     */
    public static final List<Integer> DEFAULT_BUBBLE_SORT_SAMPLE =
            List.of(5, 2, 9, 1, 5, 6);

    /** Private constructor: no instantiation needed (Spring not strictly required). */
    public DemoService() {
        // no-op
    }

    // ----- HelloWorld (§4.2) -------------------------------------------------

    /**
     * @return fixed {@code {"message":"Hello, World!"}} payload
     */
    public HelloWorldResponse helloWorld() {
        return new HelloWorldResponse(HELLO_WORLD_MESSAGE);
    }

    /**
     * Convenience accessor for the export endpoint (single column "message").
     */
    public String helloWorldMessage() {
        return HELLO_WORLD_MESSAGE;
    }

    // ----- Hash (§4.3, §5.2.3) ----------------------------------------------

    /**
     * SHA-256 hash of the given text.
     *
     * <p>Business prechecks (§5.2.2): empty/blank text and length &gt; 1000 throw
     * {@link IllegalArgumentException} which the global advice maps to code 40002.
     * {@link NoSuchAlgorithmException} is wrapped to code 50000 even though SHA-256
     * is JDK-builtin (defensive, §5.2.3).
     *
     * @param text text to hash (non-null, non-empty, length ≤ 1000)
     * @return original + algorithm + 64-char lowercase hex digest
     * @throws IllegalArgumentException when text is empty/blank or too long
     */
    public HashResponse hash(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text 不能为空");
        }
        if (text.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException("text 长度超限(≤" + MAX_TEXT_LENGTH + ")");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] digestBytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return new HashResponse(text, HASH_ALGORITHM, toLowerHex(digestBytes));
        } catch (NoSuchAlgorithmException e) {
            // Defensive: SHA-256 is JDK-builtin and should never throw this.
            throw new IllegalStateException("SHA-256 算法不可用", e);
        }
    }

    // ----- Bubble sort (§4.4) -----------------------------------------------

    /**
     * Hand-written bubble sort (NOT {@code Arrays.sort}) counting adjacent swaps.
     *
     * <p>Pre-flight correction: the design example states swaps=8 for
     * {@code [5,2,9,1,5,6]}, but that example value is WRONG. A correct bubble
     * sort on {@code [5,2,9,1,5,6]} produces swaps=6. This method implements the
     * correct algorithm and does NOT hardcode 8.
     *
     * <p>Business prechecks (§5.2.2): empty/null array and length &gt; 1000 throw
     * {@link IllegalArgumentException} → code 40002.
     *
     * @param numbers integer list (non-null, non-empty, size ≤ 1000)
     * @return input + sorted + swap count
     * @throws IllegalArgumentException when numbers is empty/null or too long
     */
    public BubbleSortResponse bubbleSort(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("numbers 不能为空");
        }
        if (numbers.size() > MAX_ARRAY_LENGTH) {
            throw new IllegalArgumentException("数组长度超限(≤" + MAX_ARRAY_LENGTH + ")");
        }

        // copy to a mutable working array
        int[] arr = new int[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            arr[i] = numbers.get(i);
        }

        int n = arr.length;
        int swaps = 0;
        // classic adjacent-swap bubble sort; early-exit when a pass makes no swaps
        for (int i = 0; i < n - 1; i++) {
            boolean swappedThisPass = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swaps++;
                    swappedThisPass = true;
                }
            }
            if (!swappedThisPass) {
                break;
            }
        }

        List<Integer> sorted = new ArrayList<>(n);
        for (int v : arr) {
            sorted.add(v);
        }
        return new BubbleSortResponse(new ArrayList<>(numbers), sorted, swaps);
    }

    // ----- Export sample data (§4.5) -----------------------------------------

    /**
     * Sample hash payload for export (no session input). Hashes the fixed
     * {@link #DEFAULT_HASH_SAMPLE_TEXT}.
     */
    public HashResponse exportHashSample() {
        return hash(DEFAULT_HASH_SAMPLE_TEXT);
    }

    /**
     * Sample bubble-sort payload for export (no session input). Sorts the fixed
     * {@link #DEFAULT_BUBBLE_SORT_SAMPLE} array.
     */
    public BubbleSortResponse exportBubbleSortSample() {
        return bubbleSort(DEFAULT_BUBBLE_SORT_SAMPLE);
    }

    // ----- helpers -----------------------------------------------------------

    /**
     * Lowercase hex encoding of a byte array.
     */
    private static String toLowerHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        final char[] HEX = "0123456789abcdef".toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            out[i * 2] = HEX[b >>> 4];
            out[i * 2 + 1] = HEX[b & 0x0F];
        }
        return new String(out);
    }
}

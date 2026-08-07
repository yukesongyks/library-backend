package com.library.service;

import com.library.dto.AlgoResult;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.util.*;

@Service
public class AlgoService {

    public AlgoResult helloworld() {
        long start = System.currentTimeMillis();
        String output = "Hello, World!";
        long duration = System.currentTimeMillis() - start;
        return new AlgoResult("helloworld", null, output, duration);
    }

    public AlgoResult hash(String input) {
        long start = System.currentTimeMillis();
        String output = sha256(input);
        long duration = System.currentTimeMillis() - start;
        return new AlgoResult("hash", input, output, duration);
    }

    public AlgoResult bubblesort(String input) {
        long start = System.currentTimeMillis();
        List<Integer> arr = parseInput(input);
        List<Integer> sorted = bubbleSort(arr);
        long duration = System.currentTimeMillis() - start;
        return new AlgoResult("bubblesort", arr, sorted, duration);
    }

    private String sha256(String base) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(base.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Integer> parseInput(String input) {
        String[] parts = input.split(",");
        List<Integer> arr = new ArrayList<>();
        for (String p : parts) {
            arr.add(Integer.parseInt(p.trim()));
        }
        return arr;
    }

    private List<Integer> bubbleSort(List<Integer> arr) {
        List<Integer> a = new ArrayList<>(arr);
        int n = a.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (a.get(j) > a.get(j + 1)) {
                    int tmp = a.get(j);
                    a.set(j, a.get(j + 1));
                    a.set(j + 1, tmp);
                }
            }
        }
        return a;
    }
}

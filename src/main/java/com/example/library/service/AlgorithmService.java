package com.example.library.service;

import com.example.library.api.AlgorithmDtos;
import com.example.library.config.UserIdentityResolver;
import com.example.library.domain.AlgorithmType;
import com.example.library.domain.InvocationEvent;
import com.example.library.domain.UserSnapshot;
import com.example.library.repository.InvocationEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
public class AlgorithmService {
    private final InvocationEventRepository eventRepository;
    private final UserIdentityResolver identityResolver;
    private final ObjectMapper objectMapper;

    public AlgorithmService(InvocationEventRepository eventRepository, UserIdentityResolver identityResolver,
                            ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.identityResolver = identityResolver;
        this.objectMapper = objectMapper;
    }

    public AlgorithmDtos.HelloWorldResponse helloWorld(AlgorithmDtos.HelloWorldRequest request,
                                                        HttpServletRequest httpRequest) {
        String requestId = UUID.randomUUID().toString();
        long started = System.nanoTime();
        UserSnapshot user = identityResolver.resolve(httpRequest);
        try {
            String name = request.name() == null || request.name().isBlank() ? "World" : request.name().trim();
            AlgorithmDtos.HelloWorldResponse response = new AlgorithmDtos.HelloWorldResponse(
                    requestId, "Hello, " + name + "!", LocalDateTime.now().toString());
            record(requestId, AlgorithmType.HELLO_WORLD, user, started, true, null, response);
            return response;
        } catch (RuntimeException exception) {
            record(requestId, AlgorithmType.HELLO_WORLD, user, started, false, "EXECUTION_ERROR", exception);
            throw exception;
        }
    }

    public AlgorithmDtos.HashResponse hash(AlgorithmDtos.HashRequest request, HttpServletRequest httpRequest) {
        String requestId = UUID.randomUUID().toString();
        long started = System.nanoTime();
        UserSnapshot user = identityResolver.resolve(httpRequest);
        try {
            String algorithm = request.algorithm() == null || request.algorithm().isBlank()
                    ? "SHA-256" : request.algorithm().trim().toUpperCase();
            if (!"SHA-256".equals(algorithm)) {
                throw new IllegalArgumentException("Only SHA-256 is supported");
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash = HexFormat.of().formatHex(digest.digest(request.text().getBytes(StandardCharsets.UTF_8)));
            AlgorithmDtos.HashResponse response = new AlgorithmDtos.HashResponse(
                    requestId, algorithm, hash, LocalDateTime.now().toString());
            record(requestId, AlgorithmType.HASH, user, started, true, null, response);
            return response;
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        } catch (RuntimeException exception) {
            record(requestId, AlgorithmType.HASH, user, started, false, "EXECUTION_ERROR", exception);
            throw exception;
        }
    }

    public AlgorithmDtos.BubbleSortResponse bubbleSort(AlgorithmDtos.BubbleSortRequest request,
                                                        HttpServletRequest httpRequest) {
        String requestId = UUID.randomUUID().toString();
        long started = System.nanoTime();
        UserSnapshot user = identityResolver.resolve(httpRequest);
        try {
            String direction = request.direction() == null || request.direction().isBlank()
                    ? "ASC" : request.direction().trim().toUpperCase();
            if (!"ASC".equals(direction) && !"DESC".equals(direction)) {
                throw new IllegalArgumentException("Direction must be ASC or DESC");
            }
            List<Integer> original = List.copyOf(request.numbers());
            List<Integer> sorted = new ArrayList<>(original);
            for (int end = sorted.size() - 1; end > 0; end--) {
                boolean swapped = false;
                for (int index = 0; index < end; index++) {
                    boolean outOfOrder = "ASC".equals(direction)
                            ? sorted.get(index) > sorted.get(index + 1)
                            : sorted.get(index) < sorted.get(index + 1);
                    if (outOfOrder) {
                        Integer value = sorted.get(index);
                        sorted.set(index, sorted.get(index + 1));
                        sorted.set(index + 1, value);
                        swapped = true;
                    }
                }
                if (!swapped) break;
            }
            AlgorithmDtos.BubbleSortResponse response = new AlgorithmDtos.BubbleSortResponse(
                    requestId, original, sorted, direction, elapsedMs(started), LocalDateTime.now().toString());
            record(requestId, AlgorithmType.BUBBLE_SORT, user, started, true, null, response);
            return response;
        } catch (RuntimeException exception) {
            record(requestId, AlgorithmType.BUBBLE_SORT, user, started, false, "EXECUTION_ERROR", exception);
            throw exception;
        }
    }

    private void record(String requestId, AlgorithmType type, UserSnapshot user, long started,
                        boolean success, String errorCode, Object payload) {
        String resultPayload;
        try {
            resultPayload = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            resultPayload = "{}";
        }
        eventRepository.save(new InvocationEvent(requestId, type, user, LocalDateTime.now(), success,
                errorCode, elapsedMs(started), resultPayload));
    }

    private long elapsedMs(long started) {
        return (System.nanoTime() - started) / 1_000_000;
    }
}

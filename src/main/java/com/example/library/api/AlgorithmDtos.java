package com.example.library.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class AlgorithmDtos {
    private AlgorithmDtos() {}

    public record HelloWorldRequest(@Size(max = 128) String name) {}

    public record HashRequest(@NotBlank @Size(max = 100_000) String text,
                              @Size(max = 32) String algorithm) {}

    public record BubbleSortRequest(@NotNull @Size(min = 1, max = 10_000) List<Integer> numbers,
                                    String direction) {}

    public record HelloWorldResponse(String requestId, String message, String executedAt) {}

    public record HashResponse(String requestId, String algorithm, String hash, String executedAt) {}

    public record BubbleSortResponse(String requestId, List<Integer> original, List<Integer> sorted,
                                     String direction, long durationMs, String executedAt) {}
}

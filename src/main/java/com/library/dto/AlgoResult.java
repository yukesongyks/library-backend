package com.library.dto;

public record AlgoResult(
    String apiName,
    Object input,
    Object output,
    long durationMs
) {}

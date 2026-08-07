package com.library.dto;

public record CallStatRow(
    String dimension,
    String value,
    long count
) {}

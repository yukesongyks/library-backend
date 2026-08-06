package com.library.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BubbleSortRequest {
    @NotNull(message = "numbers 不能为 null")
    @Size(min = 1, message = "numbers 不能为空数组")
    private List<Integer> numbers;
}

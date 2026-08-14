package com.library.demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BubbleSortRequest {
    @NotEmpty(message = "numbers is required")
    private List<Integer> numbers;
    private String order; // ASC|DESC, default ASC
}

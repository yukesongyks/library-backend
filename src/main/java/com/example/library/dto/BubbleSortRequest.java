package com.example.library.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Request DTO for bubble sort API.
 */
public class BubbleSortRequest {

    @NotNull(message = "Array must not be empty and length must not exceed 100")
    @Size(min = 1, max = 100, message = "Array must not be empty and length must not exceed 100")
    private List<Integer> array;

    public BubbleSortRequest() {
    }

    public BubbleSortRequest(List<Integer> array) {
        this.array = array;
    }

    public List<Integer> getArray() {
        return array;
    }

    public void setArray(List<Integer> array) {
        this.array = array;
    }
}
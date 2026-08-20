package com.library.demo.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class BubbleSortRequest {
    @NotEmpty(message = "numbers array is required")
    @Size(max = 1000, message = "array length must not exceed 1000")
    private List<Integer> numbers;
    private String order; // ASC | DESC, default ASC

    public List<Integer> getNumbers() { return numbers; }
    public void setNumbers(List<Integer> numbers) { this.numbers = numbers; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
}

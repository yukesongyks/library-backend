package com.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class BubbleSortRequest {
    @NotNull(message = "array不能为空")
    @Size(min = 2, max = 1000, message = "数组长度需在2~1000之间")
    private int[] array;
    private String order = "asc";

    private static final Set<String> VALID_ORDERS = Set.of("asc", "desc");

    public void validate() {
        if (!VALID_ORDERS.contains(order)) {
            throw new IllegalArgumentException("排序方向仅支持 asc 或 desc");
        }
    }
}
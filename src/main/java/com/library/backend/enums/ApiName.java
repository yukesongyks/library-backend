package com.library.backend.enums;

public enum ApiName {
    HELLO("hello"),
    HASH("hash"),
    BUBBLE_SORT("bubble-sort");

    private final String value;

    ApiName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ApiName fromValue(String value) {
        for (ApiName name : values()) {
            if (name.value.equals(value)) {
                return name;
            }
        }
        throw new IllegalArgumentException("Unknown API name: " + value);
    }
}

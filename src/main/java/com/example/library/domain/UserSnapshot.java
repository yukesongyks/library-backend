package com.example.library.domain;

public record UserSnapshot(String userId, String userType, String userLevel,
                           String departmentId, String departmentName) {}

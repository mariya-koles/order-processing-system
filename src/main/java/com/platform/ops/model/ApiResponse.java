package com.platform.ops.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorResponse error;
    private final String timestamp;
    private final String requestId;

    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.error = null;
        this.timestamp = Instant.now().toString();
        this.requestId = UUID.randomUUID().toString();
    }

    public ApiResponse(ErrorResponse error) {
        this.success = false;
        this.data = null;
        this.error = error;
        this.timestamp = Instant.now().toString();
        this.requestId = UUID.randomUUID().toString();
    }

}
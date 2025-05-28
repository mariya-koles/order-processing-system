package com.platform.ops.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private String status;
    private String destination;
    private String message;
}


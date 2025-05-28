package com.platform.ops.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    @NotBlank(message = "Destination must not be blank")
    private String destination;

    @NotBlank(message = "Content must not be blank")
    private String content;
}


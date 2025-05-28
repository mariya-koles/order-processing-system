package com.platform.ops.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String destination;
    private String content;

}

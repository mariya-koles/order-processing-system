package com.platform.ops.exception;


public class MessageSendFailureException extends RuntimeException {
    public MessageSendFailureException(String destination, Throwable cause) {
        super("Failed to send message to destination: " + destination, cause);
    }
}

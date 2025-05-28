package com.platform.ops.controller;

import com.platform.ops.exception.MessageSendFailureException;
import com.platform.ops.exception.QueueNotFoundException;
import com.platform.ops.jms.MessageProducer;
import com.platform.ops.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducer messageProducer;

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<String>> sendOrder(@RequestBody @Valid Order order) {
        try {
            messageProducer.sendOrder(order);
            return ResponseEntity.ok(new ApiResponse<>("Order queued successfully: " + order.getOrderId()));

        } catch (QueueNotFoundException | MessageSendFailureException ex) {
            log.warn("Queue error: {}", ex.getMessage());
            ErrorResponse error = new ErrorResponse("Queue Error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(error));

        } catch (Exception ex) {
            log.error("Unexpected error", ex);
            ErrorResponse error = new ErrorResponse("Internal Server Error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }

    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @RequestBody @Valid MessageRequest request) {
        try {
            messageProducer.sendMessage(request.getDestination(), request.getContent());

            MessageResponse response = new MessageResponse(
                    "Message sent successfully",
                    request.getDestination(),
                    request.getContent()
            );

            return ResponseEntity.ok(new ApiResponse<>(response));

        } catch (QueueNotFoundException | MessageSendFailureException ex) {
            log.warn("Queue error: {}", ex.getMessage());
            ErrorResponse error = new ErrorResponse("Queue Error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(error));

        } catch (Exception ex) {
            log.error("Unexpected error", ex);
            ErrorResponse error = new ErrorResponse("Internal Server Error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(error));
        }
    }
}

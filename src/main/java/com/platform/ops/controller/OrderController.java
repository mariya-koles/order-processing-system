package com.platform.ops.controller;

import com.platform.ops.model.Order;
import com.platform.ops.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        orderService.sendOrder(order);
        return ResponseEntity.ok("Order placed successfully!");
    }
}

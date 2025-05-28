package com.platform.ops.service;

import com.platform.ops.model.Order;
import jakarta.jms.Queue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final JmsTemplate jmsTemplate;
    private final Queue orderQueue;

    public OrderService(JmsTemplate jmsTemplate, Queue orderQueue) {
        this.jmsTemplate = jmsTemplate;
        this.orderQueue = orderQueue;
    }

    public void sendOrder(Order order) {
        jmsTemplate.convertAndSend(orderQueue, order);
        System.out.println("Order sent to queue: " + order);
    }
}

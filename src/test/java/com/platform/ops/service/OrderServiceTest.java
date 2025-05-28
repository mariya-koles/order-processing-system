package com.platform.ops.service;

import com.platform.ops.model.Order;
import jakarta.jms.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Tests")
class OrderServiceTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Queue orderQueue;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(jmsTemplate, orderQueue);
    }

    @Test
    @DisplayName("Should send order to JMS queue successfully")
    void shouldSendOrderToJmsQueueSuccessfully() {
        // Given
        Order order = createTestOrder();

        // When
        orderService.sendOrder(order);

        // Then
        verify(jmsTemplate, times(1)).convertAndSend(orderQueue, order);
        verifyNoMoreInteractions(jmsTemplate);
    }

    @Test
    @DisplayName("Should handle multiple orders sent to queue")
    void shouldHandleMultipleOrdersSentToQueue() {
        // Given
        Order order1 = createTestOrder();
        order1.setOrderId("ORD-001");
        
        Order order2 = createTestOrder();
        order2.setOrderId("ORD-002");
        order2.setCustomerName("Jane Smith");

        // When
        orderService.sendOrder(order1);
        orderService.sendOrder(order2);

        // Then
        verify(jmsTemplate, times(1)).convertAndSend(orderQueue, order1);
        verify(jmsTemplate, times(1)).convertAndSend(orderQueue, order2);
        verify(jmsTemplate, times(2)).convertAndSend(eq(orderQueue), any(Order.class));
    }

    @Test
    @DisplayName("Should send order with all fields populated")
    void shouldSendOrderWithAllFieldsPopulated() {
        // Given
        Order order = new Order();
        order.setOrderId("ORD-FULL-001");
        order.setCustomerName("Full Test Customer");
        order.setProduct("Complete Product");
        order.setQuantity(5);
        order.setPrice(1299.99);

        // When
        orderService.sendOrder(order);

        // Then
        verify(jmsTemplate).convertAndSend(orderQueue, order);
        
        // Verify the order object passed has all the expected values
        verify(jmsTemplate).convertAndSend(eq(orderQueue), argThat((Order sentOrder) -> 
            sentOrder.getOrderId().equals("ORD-FULL-001") &&
            sentOrder.getCustomerName().equals("Full Test Customer") &&
            sentOrder.getProduct().equals("Complete Product") &&
            sentOrder.getQuantity().equals(5) &&
            sentOrder.getPrice() == 1299.99
        ));
    }

    @Test
    @DisplayName("Should handle order with minimum valid values")
    void shouldHandleOrderWithMinimumValidValues() {
        // Given
        Order order = new Order();
        order.setOrderId("MIN-001");
        order.setCustomerName("Min Customer");
        order.setProduct("Min"); // Minimum 3 characters
        order.setQuantity(1);    // Minimum 1
        order.setPrice(0.01);    // Minimum 0.01

        // When
        orderService.sendOrder(order);

        // Then
        verify(jmsTemplate).convertAndSend(orderQueue, order);
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setOrderId("TEST-001");
        order.setCustomerName("Test Customer");
        order.setProduct("Test Product");
        order.setQuantity(2);
        order.setPrice(99.99);
        return order;
    }
} 
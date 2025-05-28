package com.platform.ops.config;

import jakarta.jms.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Configuration Tests")
class OrderConfigTest {

    private OrderConfig orderConfig;

    @BeforeEach
    void setUp() {
        orderConfig = new OrderConfig();
    }

    @Test
    @DisplayName("Should create order queue bean")
    void shouldCreateOrderQueueBean() {
        // When
        Queue orderQueue = orderConfig.orderQueue();

        // Then
        assertNotNull(orderQueue);
        assertEquals("ActiveMQQueue", orderQueue.getClass().getSimpleName());
    }

    @Test
    @DisplayName("Should create DLQ queue bean")
    void shouldCreateDlqQueueBean() {
        // When
        Queue dlqQueue = orderConfig.orderDLQ();

        // Then
        assertNotNull(dlqQueue);
        assertEquals("ActiveMQQueue", dlqQueue.getClass().getSimpleName());
    }

    @Test
    @DisplayName("Should create different queue instances")
    void shouldCreateDifferentQueueInstances() {
        // When
        Queue orderQueue1 = orderConfig.orderQueue();
        Queue orderQueue2 = orderConfig.orderQueue();
        Queue dlqQueue = orderConfig.orderDLQ();

        // Then
        assertNotNull(orderQueue1);
        assertNotNull(orderQueue2);
        assertNotNull(dlqQueue);
        
        // Each call should create a new instance (not singleton)
        assertNotSame(orderQueue1, orderQueue2);
        assertNotSame(orderQueue1, dlqQueue);
    }

    @Test
    @DisplayName("Should have order configuration class properly annotated")
    void shouldHaveOrderConfigurationClassProperlyAnnotated() {
        // When
        Class<OrderConfig> configClass = OrderConfig.class;

        // Then
        assertNotNull(configClass);
        assertTrue(configClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("Should create queues with different names")
    void shouldCreateQueuesWithDifferentNames() {
        // When
        Queue orderQueue = orderConfig.orderQueue();
        Queue dlqQueue = orderConfig.orderDLQ();

        // Then
        assertNotNull(orderQueue);
        assertNotNull(dlqQueue);
        assertNotSame(orderQueue, dlqQueue);
        
        // Verify they are different queue instances
        assertNotEquals(orderQueue.toString(), dlqQueue.toString());
    }
} 
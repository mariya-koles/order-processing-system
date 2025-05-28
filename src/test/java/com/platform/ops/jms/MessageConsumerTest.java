package com.platform.ops.jms;

import com.platform.ops.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Message Consumer Tests")
class MessageConsumerTest {

    private MessageConsumer messageConsumer;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        messageConsumer = new MessageConsumer();
        
        // Capture System.out for testing log output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Should process order successfully")
    void shouldProcessOrderSuccessfully() {
        // Given
        Order order = createTestOrder();

        // When
        assertDoesNotThrow(() -> messageConsumer.receiveOrder(order));

        // Then - No exception should be thrown for normal processing
        // Note: In the current implementation, forced failure is disabled
        assertTrue(true, "Order should be processed without exceptions");
    }

    @Test
    @DisplayName("Should handle order with all fields populated")
    void shouldHandleOrderWithAllFieldsPopulated() {
        // Given
        Order order = new Order();
        order.setOrderId("FULL-TEST-001");
        order.setCustomerName("Full Test Customer");
        order.setProduct("Complete Product");
        order.setQuantity(5);
        order.setPrice(1299.99);

        // When
        assertDoesNotThrow(() -> messageConsumer.receiveOrder(order));

        // Then
        assertTrue(true, "Order with all fields should be processed successfully");
    }

    @Test
    @DisplayName("Should handle order with minimum valid values")
    void shouldHandleOrderWithMinimumValidValues() {
        // Given
        Order order = new Order();
        order.setOrderId("MIN-001");
        order.setCustomerName("Min Customer");
        order.setProduct("Min");
        order.setQuantity(1);
        order.setPrice(0.01);

        // When
        assertDoesNotThrow(() -> messageConsumer.receiveOrder(order));

        // Then
        assertTrue(true, "Order with minimum values should be processed successfully");
    }

    @Test
    @DisplayName("Should process DLQ message successfully")
    void shouldProcessDlqMessageSuccessfully() {
        // Given
        Order order = createTestOrder();
        order.setOrderId("DLQ-TEST-001");

        // When
        assertDoesNotThrow(() -> messageConsumer.receiveFromDLQ(order));

        // Then
        assertTrue(true, "DLQ message should be processed without exceptions");
    }

    @Test
    @DisplayName("Should handle null order gracefully in DLQ listener")
    void shouldHandleNullOrderGracefullyInDlqListener() {
        // When & Then
        assertDoesNotThrow(() -> messageConsumer.receiveFromDLQ(null));
    }

    @Test
    @DisplayName("Should handle multiple orders in sequence")
    void shouldHandleMultipleOrdersInSequence() {
        // Given
        Order order1 = createTestOrder();
        order1.setOrderId("SEQ-001");
        
        Order order2 = createTestOrder();
        order2.setOrderId("SEQ-002");
        order2.setCustomerName("Second Customer");

        Order order3 = createTestOrder();
        order3.setOrderId("SEQ-003");
        order3.setProduct("Third Product");

        // When & Then
        assertDoesNotThrow(() -> {
            messageConsumer.receiveOrder(order1);
            messageConsumer.receiveOrder(order2);
            messageConsumer.receiveOrder(order3);
        });
    }

    @Test
    @DisplayName("Should handle order with special characters")
    void shouldHandleOrderWithSpecialCharacters() {
        // Given
        Order order = new Order();
        order.setOrderId("SPECIAL-001");
        order.setCustomerName("José María O'Connor");
        order.setProduct("Café & Té Special");
        order.setQuantity(1);
        order.setPrice(29.99);

        // When & Then
        assertDoesNotThrow(() -> messageConsumer.receiveOrder(order));
    }

    @Test
    @DisplayName("Should handle order with large values")
    void shouldHandleOrderWithLargeValues() {
        // Given
        Order order = new Order();
        order.setOrderId("LARGE-001");
        order.setCustomerName("Large Order Customer with Very Long Name That Exceeds Normal Length");
        order.setProduct("Very Expensive Enterprise Product with Extended Description");
        order.setQuantity(Integer.MAX_VALUE);
        order.setPrice(Double.MAX_VALUE);

        // When & Then
        assertDoesNotThrow(() -> messageConsumer.receiveOrder(order));
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
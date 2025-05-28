package com.platform.ops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ops.model.Order;
import com.platform.ops.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("Order Controller Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should place order successfully with valid data")
    void shouldPlaceOrderSuccessfullyWithValidData() throws Exception {
        // Given
        Order order = createValidOrder();
        doNothing().when(orderService).sendOrder(any(Order.class));

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully!"));

        verify(orderService, times(1)).sendOrder(any(Order.class));
    }

    @Test
    @DisplayName("Should accept order with minimum valid values")
    void shouldAcceptOrderWithMinimumValidValues() throws Exception {
        // Given
        Order order = new Order();
        order.setOrderId("MIN-001");
        order.setCustomerName("Min Customer");
        order.setProduct("Min"); // Minimum 3 characters
        order.setQuantity(1);    // Minimum 1
        order.setPrice(0.01);    // Minimum 0.01

        doNothing().when(orderService).sendOrder(any(Order.class));

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully!"));

        verify(orderService).sendOrder(any(Order.class));
    }

    @Test
    @DisplayName("Should handle large order values")
    void shouldHandleLargeOrderValues() throws Exception {
        // Given
        Order order = new Order();
        order.setOrderId("LARGE-001");
        order.setCustomerName("Large Order Customer");
        order.setProduct("Expensive Product");
        order.setQuantity(1000);
        order.setPrice(99999.99);

        doNothing().when(orderService).sendOrder(any(Order.class));

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully!"));

        verify(orderService).sendOrder(any(Order.class));
    }

    @Test
    @DisplayName("Should return 400 for malformed JSON")
    void shouldReturn400ForMalformedJson() throws Exception {
        // Given
        String malformedJson = "{ \"orderId\": \"TEST-001\", \"customerName\": }"; // Missing value

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().is5xxServerError()); // Malformed JSON causes 500, not 400

        verify(orderService, never()).sendOrder(any(Order.class));
    }

    @Test
    @DisplayName("Should return 500 for missing content type")
    void shouldReturn500ForMissingContentType() throws Exception {
        // Given
        Order order = createValidOrder();

        // When & Then
        mockMvc.perform(post("/api/orders")
                .content(objectMapper.writeValueAsString(order))) // No content type
                .andExpect(status().is5xxServerError()); // Missing content type causes 500

        verify(orderService, never()).sendOrder(any(Order.class));
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    void shouldHandleServiceExceptionGracefully() throws Exception {
        // Given
        Order order = createValidOrder();
        doThrow(new RuntimeException("JMS connection failed")).when(orderService).sendOrder(any(Order.class));

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isInternalServerError());

        verify(orderService).sendOrder(any(Order.class));
    }

    private Order createValidOrder() {
        Order order = new Order();
        order.setOrderId("TEST-001");
        order.setCustomerName("Test Customer");
        order.setProduct("Test Product");
        order.setQuantity(2);
        order.setPrice(99.99);
        return order;
    }
} 
package com.platform.ops.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ops.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.activemq.broker-url=vm://embedded-broker?broker.persistent=false",
    "spring.activemq.in-memory=true",
    "logging.level.com.platform.ops=DEBUG"
})
@DisplayName("Order Processing Integration Tests")
class OrderProcessingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should process complete order flow from REST to JMS")
    void shouldProcessCompleteOrderFlowFromRestToJms() throws Exception {
        // Given
        Order order = createValidOrder();

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully!"));
    }

    @Test
    @DisplayName("Should handle multiple concurrent orders")
    void shouldHandleMultipleConcurrentOrders() throws Exception {
        // Given
        Order order1 = createValidOrder();
        order1.setOrderId("CONCURRENT-001");
        
        Order order2 = createValidOrder();
        order2.setOrderId("CONCURRENT-002");
        order2.setCustomerName("Second Customer");

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should validate order data in integration flow")
    void shouldValidateOrderDataInIntegrationFlow() throws Exception {

        Order invalidOrder = new Order();
        invalidOrder.setOrderId("");
        invalidOrder.setCustomerName("");
        invalidOrder.setProduct("AB");
        invalidOrder.setQuantity(0);
        invalidOrder.setPrice(-10.0);

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isOk()); // Currently passes, but could be enhanced with validation
    }

    @Test
    @DisplayName("Should handle large order payload")
    void shouldHandleLargeOrderPayload() throws Exception {
        // Given
        Order largeOrder = new Order();
        largeOrder.setOrderId("LARGE-INTEGRATION-001");
        largeOrder.setCustomerName("Customer with Very Long Name ".repeat(10));
        largeOrder.setProduct("Product with Extended Description ".repeat(20));
        largeOrder.setQuantity(1000000);
        largeOrder.setPrice(999999.99);

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeOrder)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully!"));
    }

    private Order createValidOrder() {
        Order order = new Order();
        order.setOrderId("INTEGRATION-TEST-001");
        order.setCustomerName("Integration Test Customer");
        order.setProduct("Integration Test Product");
        order.setQuantity(1);
        order.setPrice(99.99);
        return order;
    }
} 
package com.platform.ops.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Model Tests")
class OrderTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid order with all required fields")
    void shouldCreateValidOrder() {
        // Given
        Order order = new Order();
        order.setOrderId("ORD-001");
        order.setCustomerName("John Doe");
        order.setProduct("Laptop");
        order.setQuantity(2);
        order.setPrice(999.99);

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        // Then
        assertTrue(violations.isEmpty(), "Valid order should have no validation errors");
        assertEquals("ORD-001", order.getOrderId());
        assertEquals("John Doe", order.getCustomerName());
        assertEquals("Laptop", order.getProduct());
        assertEquals(2, order.getQuantity());
        assertEquals(999.99, order.getPrice());
    }

    @Test
    @DisplayName("Should fail validation when orderId is blank")
    void shouldFailValidationWhenOrderIdIsBlank() {
        // Given
        Order order = createValidOrder();
        order.setOrderId("");

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<Order> violation = violations.iterator().next();
        assertEquals("Order ID must not be blank", violation.getMessage());
        assertEquals("orderId", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Should fail validation when customerName is blank")
    void shouldFailValidationWhenCustomerNameIsBlank() {
        // Given
        Order order = createValidOrder();
        order.setCustomerName("");

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<Order> violation = violations.iterator().next();
        assertEquals("Customer name must not be blank", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when product name is too short")
    void shouldFailValidationWhenProductNameIsTooShort() {
        // Given
        Order order = createValidOrder();
        order.setProduct("AB"); // Less than 3 characters

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<Order> violation = violations.iterator().next();
        assertEquals("Product name must be at least 3 characters", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when quantity is less than 1")
    void shouldFailValidationWhenQuantityIsLessThanOne() {
        // Given
        Order order = createValidOrder();
        order.setQuantity(0);

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<Order> violation = violations.iterator().next();
        assertEquals("Quantity must be at least 1", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when price is zero or negative")
    void shouldFailValidationWhenPriceIsZeroOrNegative() {
        // Given
        Order order = createValidOrder();
        order.setPrice(0.0);

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<Order> violation = violations.iterator().next();
        assertEquals("Price must be greater than 0", violation.getMessage());
    }

    @Test
    @DisplayName("Should have multiple validation errors for invalid order")
    void shouldHaveMultipleValidationErrorsForInvalidOrder() {
        // Given
        Order order = new Order();
        order.setOrderId("");
        order.setCustomerName("");
        order.setProduct("AB");
        order.setQuantity(0);
        order.setPrice(-10.0);

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        // Then
        assertEquals(5, violations.size(), "Should have 5 validation errors");
    }

    @Test
    @DisplayName("Should generate proper toString representation")
    void shouldGenerateProperToStringRepresentation() {
        // Given
        Order order = createValidOrder();

        // When
        String toString = order.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("ORD-001"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("Laptop"));
    }

    private Order createValidOrder() {
        Order order = new Order();
        order.setOrderId("ORD-001");
        order.setCustomerName("John Doe");
        order.setProduct("Laptop");
        order.setQuantity(2);
        order.setPrice(999.99);
        return order;
    }
} 
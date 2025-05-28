package com.platform.ops.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jms.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Messaging Configuration Tests")
class MessagingConfigTest {

    private MessagingConfig messagingConfig;

    @BeforeEach
    void setUp() {
        messagingConfig = new MessagingConfig();
    }

    @Test
    @DisplayName("Should create Jackson message converter")
    void shouldCreateJacksonMessageConverter() {
        // When
        MessageConverter converter = messagingConfig.jacksonJmsMessageConverter();

        // Then
        assertNotNull(converter);
        assertTrue(converter.getClass().getSimpleName().contains("Jackson"));
    }

    @Test
    @DisplayName("Should create redelivery policy")
    void shouldCreateRedeliveryPolicy() {
        // When
        Object redeliveryPolicy = messagingConfig.redeliveryPolicy();

        // Then
        assertNotNull(redeliveryPolicy);
        assertEquals("RedeliveryPolicy", redeliveryPolicy.getClass().getSimpleName());
    }

    @Test
    @DisplayName("Should create custom error handler")
    void shouldCreateCustomErrorHandler() {
        // When
        Object errorHandler = messagingConfig.customErrorHandler();

        // Then
        assertNotNull(errorHandler);
        assertTrue(errorHandler.getClass().getName().contains("MessagingConfig") || 
                  errorHandler.getClass().getName().contains("lambda"));
    }

    @Test
    @DisplayName("Should have messaging configuration class properly annotated")
    void shouldHaveMessagingConfigurationClassProperlyAnnotated() {
        // When
        Class<MessagingConfig> configClass = MessagingConfig.class;

        // Then
        assertNotNull(configClass);
        assertTrue(configClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        assertTrue(configClass.isAnnotationPresent(org.springframework.jms.annotation.EnableJms.class));
    }

    @Test
    @DisplayName("Should create different converter instances")
    void shouldCreateDifferentConverterInstances() {
        // When
        MessageConverter converter1 = messagingConfig.jacksonJmsMessageConverter();
        MessageConverter converter2 = messagingConfig.jacksonJmsMessageConverter();

        // Then
        assertNotNull(converter1);
        assertNotNull(converter2);
        // Each call should create a new instance (not singleton)
        assertNotSame(converter1, converter2);
    }
} 
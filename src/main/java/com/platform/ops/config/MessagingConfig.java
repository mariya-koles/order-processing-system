package com.platform.ops.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.*;
import org.springframework.util.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableJms
public class MessagingConfig {

    private static final Logger log = LoggerFactory.getLogger(MessagingConfig.class);

    @Bean
    public RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy policy = new RedeliveryPolicy();
        policy.setMaximumRedeliveries(2);     // 2 redeliveries = 3 total attempts (1 initial + 2 retries)
        policy.setInitialRedeliveryDelay(2000); // Wait 2 seconds before retry
        policy.setRedeliveryDelay(2000);
        policy.setUseExponentialBackOff(false);
        
        // Ensure DLQ processing is enabled
        policy.setQueue("*.DLQ");  // Send failed messages to DLQ
        
        log.info("RedeliveryPolicy configured: maxRedeliveries={}, initialDelay={}ms", 
                policy.getMaximumRedeliveries(), policy.getInitialRedeliveryDelay());
        
        return policy;
    }

    @Bean
    @Primary
    public ActiveMQConnectionFactory activeMQConnectionFactory(RedeliveryPolicy redeliveryPolicy) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        factory.setUserName("admin");
        factory.setPassword("admin");
        factory.setRedeliveryPolicy(redeliveryPolicy);
        
        // Enable DLQ processing
        factory.setWatchTopicAdvisories(false);
        
        log.info("ActiveMQ ConnectionFactory configured with RedeliveryPolicy");
        
        return factory;
    }

    @Bean
    public ErrorHandler customErrorHandler() {
        return throwable -> {
            log.error("Error in JMS listener - this should trigger DLQ after retries: {}", throwable.getMessage());
            throw new RuntimeException(throwable);
        };
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            @Qualifier("activeMQConnectionFactory") ActiveMQConnectionFactory activeMQConnectionFactory,
            ErrorHandler customErrorHandler) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory);
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setErrorHandler(customErrorHandler);
        factory.setSessionAcknowledgeMode(jakarta.jms.Session.CLIENT_ACKNOWLEDGE);
        factory.setConcurrency("1-1");      // ensure one consumer for testing
        
        // Enable auto-startup
        factory.setAutoStartup(true);

        log.info("JMS Listener Container Factory configured with CLIENT_ACKNOWLEDGE mode");

        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
        jmsTemplate.setDeliveryPersistent(true);
        return jmsTemplate;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT); // Use JSON instead of Java binary
        converter.setTypeIdPropertyName("_type");  // deserialize properly
        return converter;
    }
}

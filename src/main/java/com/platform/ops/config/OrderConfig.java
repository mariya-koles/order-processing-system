package com.platform.ops.config;

import jakarta.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

    @Bean
    public Queue orderQueue() {
        return new ActiveMQQueue("orders.queue");
    }
    
    @Bean
    public Queue orderDLQ() {
        return new ActiveMQQueue("orders.DLQ");
    }
}

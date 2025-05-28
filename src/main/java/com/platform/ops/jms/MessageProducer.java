package com.platform.ops.jms;

import com.platform.ops.exception.MessageSendFailureException;
import com.platform.ops.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);
    private final JmsTemplate jmsTemplate;

    public void sendOrder(Order order) {
        log.info("Sending order: {}", order);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.convertAndSend("orders.queue", order);
    }
    public void sendMessage(String destination, String message) {
        try {
            log.info("Sending message to '{}': {}", destination, message);
            jmsTemplate.convertAndSend(destination, message);
        } catch (JmsException e) {
            log.error("Failed to send message to {}", destination, e);
            throw new MessageSendFailureException(destination, e);
        }
    }
}

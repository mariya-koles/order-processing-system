package com.platform.ops.jms;
import com.platform.ops.model.Order;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class MessageConsumer {
    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    @JmsListener(destination = "demo.queue")
    public void receiveMessage(String message) {
        log.info("Received message: {}", message);
    };

    @JmsListener(destination = "orders.queue")
    public void receiveOrder(Order order) {
        log.info("Received order: {}", order);
    }

    @JmsListener(destination = "orders.DLQ")
    public void receiveFromDLQ(Object message) {
        log.error("🔥 DLQ LISTENER CALLED! Message type: {}", 
                message != null ? message.getClass().getSimpleName() : "null");
        if (message instanceof Order order) {
            log.error("ERROR: Order in DLQ: {}", order);
        } else if (message != null) {
            log.error("ERROR: Non-order message in DLQ: {}", message);
        } else {
            log.error("ERROR: Null message received in DLQ");
        }
    }
}

package org.example.producer;

import org.example.event.OrderCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Profile("producer")
@Service
public class OrderProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String topic;

    public OrderProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
                         @Value("${topic.orderCreated}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishOrderCreated(String orderId, String userId, long totalAmount) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                orderId,
                userId,
                totalAmount,
                Instant.now()
        );

        // Key = orderId => giữ thứ tự theo từng orderId
        kafkaTemplate.send(topic, orderId, event);
    }
}

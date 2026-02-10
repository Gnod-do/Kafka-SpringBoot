package org.example.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.event.OrderCreatedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Profile("consumer")
@Component
public class NotificationConsumer {

    private final ProcessedEventRepository repo;

    public NotificationConsumer(ProcessedEventRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "${topic.orderCreated}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, OrderCreatedEvent> record, Acknowledgment ack) {
        OrderCreatedEvent event = record.value();

        try {
            // Idempotency check: đã xử lý eventId này chưa?
            if (repo.existsById(event.eventId())) {
                System.out.println("[SKIP] duplicate eventId=" + event.eventId());
                ack.acknowledge();
                return;
            }

            // "Business" xử lý (giả lập)
            System.out.printf(
                    "[PROCESS] partition=%d offset=%d key=%s eventId=%s orderId=%s total=%d%n",
                    record.partition(), record.offset(), record.key(),
                    event.eventId(), event.orderId(), event.totalAmount()
            );

            // Lưu dấu đã xử lý
            repo.save(new ProcessedEvent(event.eventId(), Instant.now()));

            // Xử lý xong mới commit offset
            ack.acknowledge();

        } catch (Exception e) {
            // Không ack => Kafka sẽ deliver lại (at-least-once)
            System.err.println("[ERROR] " + e.getMessage());
            throw e;
        }
    }
}

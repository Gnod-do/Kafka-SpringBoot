package org.example.event;

import java.time.Instant;

public record OrderCreatedEvent(String eventId,
                               String orderId,
                               String userId,
                               long totalAmount,
                               Instant createdAt) {
}

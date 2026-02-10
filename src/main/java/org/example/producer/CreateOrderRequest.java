package org.example.producer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank String orderId,
        @NotBlank String userId,
        @Min(1) long totalAmount
) {
}

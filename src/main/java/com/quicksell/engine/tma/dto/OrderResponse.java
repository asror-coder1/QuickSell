package com.quicksell.engine.tma.dto;

import com.quicksell.engine.order.OrderStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        BigDecimal totalAmount,
        OrderStatus status
) {
}

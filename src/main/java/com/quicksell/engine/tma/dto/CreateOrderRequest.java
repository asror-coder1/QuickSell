package com.quicksell.engine.tma.dto;

import com.quicksell.engine.order.PaymentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID shopId,
        @NotNull Long customerTgId,
        @NotBlank String customerPhone,
        @NotNull PaymentType paymentType,
        @Valid @NotEmpty List<OrderItemRequest> items
) {
}
